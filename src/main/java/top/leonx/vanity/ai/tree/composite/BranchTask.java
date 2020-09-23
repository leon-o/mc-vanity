package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

public class BranchTask<T extends LivingEntity> extends CompositeTask<T> {


    public BranchTask(String name) {
        super(name);
    }

    public BehaviorTreeTask<T> condition;
    public BehaviorTreeTask<T> whenSuccess;
    public BehaviorTreeTask<T> whenFail;

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        conditionRunFinished=false;
        condition.callForStart(world, entity, executionDuration);
    }
    boolean conditionRunFinished=false;
    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(condition.getResult()==Result.RUNNING)
        {
            condition.callForUpdate(world, entity, executionDuration);
        }else {
            if(!conditionRunFinished)
                condition.callForEnd(world, entity, executionDuration);

            if (condition.getResult()== Result.SUCCESS) {
                if(whenSuccess.getResult()!=Result.RUNNING)
                    whenSuccess.callForStart(world, entity, executionDuration);
                else
                    whenSuccess.callForUpdate(world, entity, executionDuration);
            }else
            {
                if(whenFail.getResult()!=Result.RUNNING)
                    whenFail.callForStart(world, entity, executionDuration);
                else
                    whenFail.callForUpdate(world, entity, executionDuration);
            }
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        if(condition.getResult()==Result.RUNNING)
            condition.callForEnd(world, entity, executionDuration);
        else if(whenSuccess.getResult()==Result.RUNNING)
            whenSuccess.callForEnd(world, entity, executionDuration);
        else if(whenFail.getResult()==Result.RUNNING)
            whenFail.callForEnd(world, entity, executionDuration);
    }
}
