package top.leonx.vanity.ai.tree.leaf.Instantaneous;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("NullableProblems")
public class FindAttackTargetTask<T extends MobEntity> extends BehaviorTreeTask<T> {
    private final Predicate<Entity> filter;
    public FindAttackTargetTask(Predicate<Entity> filter) {

        this.filter=filter;
    }

    @Override
    protected void onStart(ServerWorld world, T entityIn, long executionDuration) {
        if(!entityIn.getBrain().hasMemory(MemoryModuleType.VISIBLE_MOBS, MemoryModuleStatus.VALUE_PRESENT))
            submitResult(Result.FAIL);

        Optional<List<LivingEntity>> visibleMobsOptional = entityIn.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if(visibleMobsOptional.isPresent())
        {
            List<LivingEntity> visibleMobs = visibleMobsOptional.get();
            Optional<LivingEntity> closest = visibleMobs.stream().filter(filter).min(Comparator.comparingDouble(t -> t.getDistanceSq(entityIn)));
            closest.ifPresent(entityIn::setAttackTarget);
            submitResult(Result.SUCCESS);
        }
        submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }


}
