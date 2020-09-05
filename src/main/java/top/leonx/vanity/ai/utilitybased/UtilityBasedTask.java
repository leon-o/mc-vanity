package top.leonx.vanity.ai.utilitybased;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.util.TernaryFunc;

public abstract class UtilityBasedTask<T extends LivingEntity> {
    public TernaryFunc<ServerWorld,T,Long,Double> utilityScoreCalculator;
    private Result result;

    public Result getResult() {
        return result;
    }

    protected void submitResult(Result result) {
        this.result = result;
    }

    public UtilityBasedTask()
    {
        utilityScoreCalculator=(a,b,c)->0D;
    }
    public UtilityBasedTask(TernaryFunc<ServerWorld,T,Long,Double> calculator)
    {
        utilityScoreCalculator=calculator;
    }

    public double getUtilityScore(ServerWorld world, T entity,long executionDuration)
    {
        return utilityScoreCalculator==null?0D:utilityScoreCalculator.compute(world,entity,executionDuration);
    }
    public void callForEnd(ServerWorld world, T entity,long executionDuration)
    {
        submitResult(Result.SUCCESS);
        onEnd(world, entity, executionDuration);
    }
    public void callForStart(ServerWorld world, T entity, long executionDuration)
    {
        onStart(world,entity,executionDuration);
        submitResult(Result.RUNNING);
    }
    public void callForUpdate(ServerWorld world, T entity, long executionDuration)
    {
        onUpdate(world, entity, executionDuration);
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
