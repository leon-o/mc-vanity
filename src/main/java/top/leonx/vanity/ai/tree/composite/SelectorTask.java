package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.ArrayList;
import java.util.List;

public class SelectorTask<T extends LivingEntity> extends BehaviorTreeTask<T> {
    public List<BehaviorTreeTask<T>> children;
    public boolean                   continueWhenSuccess =false;
    public SelectorTask() {
        children=new ArrayList<>();
    }

    public SelectorTask(List<BehaviorTreeTask<T>> children) {
        this.children = children;
    }


    int                 runningPointer=0;
    BehaviorTreeTask<T> runningTask;
    boolean             anySuccess=false;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        runningPointer=0;
        anySuccess=false;
        if(children.size()>0){

            runningTask=children.get(runningPointer);
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
        runningTask.callForUpdate(world,entity,executionDuration);

        if(runningTask.getResult()==Result.FAIL || runningTask.getResult()==Result.SUCCESS)
        {
            runningTask.callForEnd(world,entity,executionDuration);

            anySuccess |=runningTask.getResult()==Result.SUCCESS;

            if(anySuccess && !continueWhenSuccess) {
                submitResult(Result.SUCCESS);
                return;
            }
            runningPointer++;
            if(runningPointer<children.size())
            {
                runningTask = children.get(runningPointer);
                runningTask.callForStart(world,entity,executionDuration);
            }else{
                submitResult(anySuccess ?Result.SUCCESS:Result.FAIL);
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
