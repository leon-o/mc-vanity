package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.Optional;

public class LookAtNearestTask<T extends MobEntity> extends BehaviorTreeTask<T> {

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        //Optional<LivingEntity> min = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Collections.emptyList()).stream().min(Comparator.comparingDouble(entity::getDistanceSq));
        Optional<PlayerEntity> min = entity.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        min.ifPresent(livingEntity -> entity.getLookController().setLookPositionWithEntity(livingEntity, 15, 15));
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
