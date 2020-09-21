package top.leonx.vanity.ai.tree.leaf.continuous;

import com.google.common.collect.Lists;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Objects;
import java.util.function.Function;

public class EscapeFromTask<T extends CreatureEntity> extends BehaviorTreeTask<T> {
    private Function<CreatureEntity, LivingEntity> avoidTargetGetter;


    public EscapeFromTask()
    {
        this(entity-> entity.world.getClosestEntity(entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS).orElse(Lists.newArrayList()),
                                                 new EntityPredicate().setCustomPredicate(t-> Objects.equals(t.getAttackingEntity(), entity)),
                                                 entity, entity.getPosX(), entity.getPosY(), entity.getPosZ()));
    }
    public EscapeFromTask(Function<CreatureEntity, LivingEntity> avoidTargetGetter) {
        this.avoidTargetGetter = avoidTargetGetter;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        if(avoidTargetGetter==null)
        {
            submitResult(Result.FAIL);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {

        LivingEntity avoidTarget = avoidTargetGetter.apply(entity);
        if(avoidTarget!=null && avoidTarget.isAlive()){
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(entity, 16, 7, avoidTarget.getPositionVec());
            if(vec3d==null) return;
            entity.getNavigator().tryMoveToXYZ(vec3d.x,vec3d.y,vec3d.z,1);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        entity.getNavigator().clearPath();
    }
}
