package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.List;

public class SequencesTask<T extends LivingEntity> extends CompositeTask<T> {
    public boolean                   continueWhenFail=false;
    public SequencesTask(String name) {
        super(name);
    }

    public SequencesTask(String name,List<BehaviorTreeTask<T>> children) {
        super(name);
        getChildren().addAll( children);
    }


    int                 runningPointer=0;
    BehaviorTreeTask<T> runningTask;
    boolean             allSuccess;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        runningPointer=0;
        allSuccess=false;
        if(getChildren().size()>0){
            runningTask=getChildren().get(runningPointer);
            runningTask.callForStart(world,entity,executionDuration);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(runningTask==null){
            submitResult(Result.FAIL);
            return;
        }
        if(getResult()!=Result.RUNNING) return;

        if(runningTask.getResult()==Result.RUNNING)
            runningTask.callForUpdate(world,entity,executionDuration);

        if(runningTask.getResult()==Result.FAIL || runningTask.getResult()==Result.SUCCESS)
        {
            runningTask.callForEnd(world,entity,executionDuration);

            allSuccess&=runningTask.getResult()==Result.SUCCESS;

            if(!allSuccess && !continueWhenFail) {
                submitResult(Result.FAIL);
                return;
            }
            runningPointer++;
            if(runningPointer<getChildren().size())
            {
                runningTask = getChildren().get(runningPointer);
                runningTask.callForStart(world,entity,executionDuration);
            }else{
                submitResult(allSuccess?Result.SUCCESS:Result.FAIL);
            }
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        if(runningTask!=null)
            runningTask.callForEnd(world,entity,executionDuration);

        runningTask=null;
    }
}
