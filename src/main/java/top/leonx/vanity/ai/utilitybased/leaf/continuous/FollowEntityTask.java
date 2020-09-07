package top.leonx.vanity.ai.utilitybased.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.util.TernaryFunc;

public class FollowEntityTask<T extends MobEntity> extends UtilityBasedTask<T> {
    public LivingEntity targetEntity;

    public FollowEntityTask(LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public FollowEntityTask(TernaryFunc<ServerWorld, T, Long, Double> calculator, LivingEntity targetEntity) {
        super(calculator);
        this.targetEntity = targetEntity;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        entity.getNavigator().tryMoveToEntityLiving(targetEntity,entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
