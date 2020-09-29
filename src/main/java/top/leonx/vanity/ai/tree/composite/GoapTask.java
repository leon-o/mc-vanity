package top.leonx.vanity.ai.tree.composite;

import net.minecraft.util.Tuple;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.goap.GoapCost;
import top.leonx.vanity.ai.goap.GoapGoal;
import top.leonx.vanity.ai.goap.PurposefulTaskRegistry;
import top.leonx.vanity.ai.goap.PurposefulTaskWrap;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.*;
import java.util.stream.Collectors;

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
            if(getLastChild().getResult()==Result.FAIL)
                submitResult(Result.FAIL);
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
        Stack<PurposefulTaskWrap> wrapChoose=new Stack<>();
        Stack<BehaviorTreeTask<OutsiderEntity>> taskChoose=new Stack<>();
        Stack<GoapGoal> goalChoose=new Stack<>();

        HashSet<PurposefulTaskWrap> deprecatedWrap=new HashSet<>();

        GoapGoal currentGoal = goal;
        while (!currentGoal.isFit.apply(entity)) {
            GoapGoal finalCurrentGoal = currentGoal;
            Optional<Tuple<PurposefulTaskWrap,GoapCost>> taskWrapMinCost = PurposefulTaskRegistry.purposefulTasks.stream()
                    .map(t -> new Tuple<>(t, t.handleGoapGoal(finalCurrentGoal)))
                    .filter(t -> !deprecatedWrap.contains(t.getA())&&!t.getB().equals(GoapCost.PASS))
                    .min(Comparator.comparingInt(t -> t.getB().getCost()));

            // Didn't find. This means previous task is unable to complete. Roll back and find another.
            if(!taskWrapMinCost.isPresent())
            {
                deprecatedWrap.add(wrapChoose.pop());
                taskChoose.pop();
                goalChoose.pop();

                if(wrapChoose.size()==0)
                    break;

                currentGoal=goalChoose.peek();
            }else{
                PurposefulTaskWrap taskWrap = taskWrapMinCost.get().getA();
                wrapChoose.push(taskWrap);
                taskChoose.push(taskWrap.task.apply(currentGoal));
                goalChoose.push(currentGoal);

                currentGoal=taskWrap.precondition.apply(currentGoal); //create next goal by this task's precondition.
            }
        }
        getChildren().addAll(taskChoose);
    }
}
