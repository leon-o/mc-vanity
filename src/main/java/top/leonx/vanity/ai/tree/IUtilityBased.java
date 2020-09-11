package top.leonx.vanity.ai.tree;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;

public interface IUtilityBased<T extends LivingEntity> {

    double getUtilityScore(ServerWorld world, T entity, long executionDuration);
}
