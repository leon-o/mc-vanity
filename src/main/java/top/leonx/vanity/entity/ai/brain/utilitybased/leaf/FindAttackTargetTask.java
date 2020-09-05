package top.leonx.vanity.entity.ai.brain.utilitybased.leaf;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class FindAttackTargetTask extends Task<OutsiderEntity> {
    private final Predicate<Entity> filter;
    public FindAttackTargetTask(Predicate<Entity> filter) {
        super(ImmutableMap.of(MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT));
        this.filter=filter;
    }


    @Override
    protected void startExecuting(ServerWorld worldIn, OutsiderEntity entityIn, long gameTimeIn) {
        Optional<List<LivingEntity>> visibleMobsOptional = entityIn.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if(visibleMobsOptional.isPresent())
        {
            List<LivingEntity> visibleMobs = visibleMobsOptional.get();
            Optional<LivingEntity> closest = visibleMobs.stream().filter(filter).min(Comparator.comparingDouble(t -> t.getDistanceSq(entityIn)));
            closest.ifPresent(entityIn::setAttackTarget);
        }
    }

}
