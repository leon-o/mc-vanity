package top.leonx.vanity.ai.tree.composite;

import net.minecraft.util.Tuple;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.goap.GoapCost;
import top.leonx.vanity.ai.goap.GoapGoal;
import top.leonx.vanity.ai.goap.PurposefulTaskRegistry;
import top.leonx.vanity.ai.goap.PurposefulTaskWrap;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;
import java.util.Optional;

public class GoapTask extends CompositeTask<OutsiderEntity> {

    public GoapGoal goal;

    public GoapTask(String name,GoapGoal goapGoal) {
        super(name);
        this.goal = goapGoal;
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        getChildren().clear();
    }

    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        plan(entity);
        if (getChildren().size() == 0) submitResult(Result.FAIL);
        else {
            getLastChild().callForStart(world, entity, executionDuration);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(getChildren().size()==0)
        {
            submitResult(Result.FAIL);
            return;
        }

        BehaviorTreeTask<OutsiderEntity> task = getLastChild();
        task.callForUpdate(world, entity, executionDuration);

        if (task.getResult() == Result.SUCCESS) {
            task.callForEnd(world, entity, executionDuration);

            getChildren().remove(getChildren().size()-1);

            if (getChildren().size()==0) {
                submitResult(Result.SUCCESS);
                return;
            }

            getLastChild().callForStart(world, entity, executionDuration);
        } else if (task.getResult() == Result.FAIL) {
            submitResult(Result.FAIL);
        }
    }

    private BehaviorTreeTask<OutsiderEntity> getLastChild()
    {
        return getChildren().get(getChildren().size()-1);
    }
    private void plan(OutsiderEntity entity) {
        getChildren().clear();

        GoapGoal currentGoal = goal;
        while (!currentGoal.isFit.apply(entity)) {
            GoapGoal finalCurrentGoal = currentGoal;
            Optional<Tuple<PurposefulTaskWrap,GoapCost>> minGoapCost = PurposefulTaskRegistry.purposefulTasks.stream().map(t -> new Tuple<>(t, t.handleGoapGoal(finalCurrentGoal))).filter(
                    t -> !t.getB().equals(GoapCost.PASS)).min(Comparator.comparingInt(t -> t.getB().getCost()));

            if (minGoapCost.isPresent()) {
                PurposefulTaskWrap               taskWrap = minGoapCost.get().getA();
                BehaviorTreeTask<OutsiderEntity> nextTask = taskWrap.task.apply(currentGoal);
                getChildren().add(nextTask);
                currentGoal = taskWrap.precondition.apply(currentGoal);
            } else {
                getChildren().clear();
                break;
            }
        }
    }
}
