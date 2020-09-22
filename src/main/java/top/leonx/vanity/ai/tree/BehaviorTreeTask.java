package top.leonx.vanity.ai.tree;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.VanityMod;

public abstract class BehaviorTreeTask<T extends LivingEntity> {
    //public TernaryFunc<ServerWorld,T,Long,Double> utilityScoreCalculator;
    private Result result;
    private int delayTicks;
    public Result getResult() {
        return result;
    }
    public String getTaskName()
    {
        return this.getClass().getName();
    }
    protected void submitResult(Result result) {
        this.result = result;
    }

    public void callForEnd(ServerWorld world, T entity,long executionDuration)
    {
        if(getResult()== Result.RUNNING)//If result wasn't set to Fail, we set it to SUCCESS.
            submitResult(Result.SUCCESS);
        onEnd(world, entity, executionDuration);
    }
    public void callForStart(ServerWorld world, T entity, long executionDuration)
    {
        submitResult(null);//There is no result before task started.

        onStart(world,entity,executionDuration);

        if(getResult()==null)//If onStart didn't submit result(Fail or Success) we set it to RUNNING.
            submitResult(Result.RUNNING);

        System.out.println("start "+this.getTaskName());
    }
    public void callForUpdate(ServerWorld world, T entity, long executionDuration)
    {
        if(delayTicks==0)
            onUpdate(world, entity, executionDuration);
        else
            delayTicks--;
    }

    /**
     * {@link BehaviorTreeTask#onUpdate(ServerWorld, LivingEntity, long)} won't be called until delay time ends
     */
    protected void setUpDelay(int tick)
    {
        delayTicks=tick;
    }
    public boolean canStart(ServerWorld world, T entity,long executionDuration)
    {
        return true;
    }
    protected abstract void onStart(ServerWorld world, T entity, long executionDuration);
    protected abstract void onUpdate(ServerWorld world, T entity, long executionDuration);
    protected abstract void onEnd(ServerWorld world, T entity, long executionDuration);

    public enum Result{
        RUNNING,
        SUCCESS,
        FAIL
    }
}
