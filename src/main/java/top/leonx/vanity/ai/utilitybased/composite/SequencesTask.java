package top.leonx.vanity.ai.utilitybased.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.util.TernaryFunc;

import java.util.ArrayList;
import java.util.List;

public class SequencesTask<T extends LivingEntity> extends UtilityBasedTask<T> {
    public List<UtilityBasedTask<T>> children;
    public boolean continueWhenFail=false;
    public SequencesTask() {
        children=new ArrayList<>();
    }

    public SequencesTask(List<UtilityBasedTask<T>> children) {
        this.children = children;
    }

    public SequencesTask(TernaryFunc<ServerWorld, T, Long, Double> calculator) {
        super(calculator);
    }
    public SequencesTask(TernaryFunc<ServerWorld, T, Long, Double> calculator,List<UtilityBasedTask<T>> children) {
        super(calculator);
        this.children = children;
    }

    int runningPointer=0;
    UtilityBasedTask<T> runningTask;
    boolean allSuccess;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        runningPointer=0;
        if(children.size()>0)
            runningTask=children.get(runningPointer);
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

            allSuccess&=runningTask.getResult()==Result.SUCCESS;

            if(!allSuccess && !continueWhenFail)
                submitResult(Result.FAIL);

            runningPointer++;
            if(runningPointer<children.size())
            {
                runningTask = children.get(runningPointer);
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
