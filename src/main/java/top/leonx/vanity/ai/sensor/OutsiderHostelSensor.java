package top.leonx.vanity.ai.sensor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.*;

public class OutsiderHostelSensor extends Sensor<OutsiderEntity> {
    @Override
    protected void update(ServerWorld worldIn, OutsiderEntity entityIn) {
        Optional<List<LivingEntity>> visibleMobsOpt = entityIn.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if (visibleMobsOpt.isPresent())
        {
            List<LivingEntity> visibleMobs=visibleMobsOpt.get();
            PlayerEntity       followedPlayer = entityIn.getFollowedPlayer();
            Optional<LivingEntity> nearestHostel = visibleMobs.stream().filter(t ->

                followedPlayer != null && (Objects.equals(t.getAttackingEntity(), followedPlayer) || (t instanceof MobEntity && Objects.equals(((MobEntity) t).getAttackTarget(),followedPlayer)))
                        || (Objects.equals( t.getAttackingEntity(), entityIn) || (t instanceof MobEntity && Objects.equals(((MobEntity) t).getAttackTarget(), entityIn)))

            ).min(Comparator.comparingDouble(entityIn::getDistanceSq));
            entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_HOSTILE,nearestHostel);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_HOSTILE);
    }
}
