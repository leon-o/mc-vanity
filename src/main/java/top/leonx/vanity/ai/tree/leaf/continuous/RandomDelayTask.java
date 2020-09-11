package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.util.TernaryFunc;

public class RandomDelayTask<T extends LivingEntity> extends BehaviorTreeTask<T> {
    private final TernaryFunc<ServerWorld,T,Long,Double> dummyUtilityScore = (w, e, t)-> Double.MAX_VALUE;
    private       TernaryFunc<ServerWorld,T,Long,Double> realScoreCalculator;
    private       int duration;
    private final int minDuration;
    private final int maxDuration;
    public RandomDelayTask(int min,int max)
    {
        minDuration =min;
        maxDuration=max;
    }

    @Override
    public void onStart(ServerWorld world, T entity, long executionDuration) {
        duration= (int) (Math.random()*(maxDuration- minDuration)+ minDuration);
    }

    @Override
    public void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(executionDuration>duration)
        {
            submitResult(Result.SUCCESS);
        }
    }

    @Override
    public void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
