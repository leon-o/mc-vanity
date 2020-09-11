package top.leonx.vanity.ai.tree;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.util.TernaryFunc;

public abstract class UtilityBasedTreeTask<T extends LivingEntity> extends BehaviorTreeTask<T> implements IUtilityBased<T>{
    TernaryFunc<ServerWorld, T,Long,Double> utilityScoreCalculator;

    public UtilityBasedTreeTask(TernaryFunc<ServerWorld, T, Long, Double> utilityScoreCalculator) {
        this.utilityScoreCalculator = utilityScoreCalculator;
    }



    @Override
    public double getUtilityScore(ServerWorld world, T entity, long executionDuration) {
        if(utilityScoreCalculator==null) return 0;
        return utilityScoreCalculator.compute(world,entity,executionDuration);
    }
}
