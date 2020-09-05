package top.leonx.vanity.entity.ai.brain.utilitybased;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.util.TernaryFunc;

public abstract class UtilityBasedTask<T extends LivingEntity> {
    public TernaryFunc<ServerWorld,T,Long,Double> utilityScoreCalculator;
    private Task.Status status;

    public Task.Status getStatus() {
        return status;
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
        status= Task.Status.STOPPED;
        ending(world,entity,executionDuration);
    }
    public void callForStart(ServerWorld world,T entity,long executionDuration)
    {
        action(world,entity,executionDuration);
    }
    public boolean canStart(ServerWorld world, T entity,long executionDuration)
    {
        return true;
    }
    public abstract void action(ServerWorld world, T entity,long executionDuration);
    public abstract void ending(ServerWorld world, T entity,long executionDuration);
}
