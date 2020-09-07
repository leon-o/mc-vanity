package top.leonx.vanity.ai.utilitybased.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.util.TernaryFunc;

public class MoveToEntityTask<T extends MobEntity> extends UtilityBasedTask<T> {
    public LivingEntity targetEntity;

    public MoveToEntityTask(LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }

    public MoveToEntityTask(TernaryFunc<ServerWorld, T, Long, Double> calculator, LivingEntity targetEntity) {
        super(calculator);
        this.targetEntity = targetEntity;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(targetEntity==null || !targetEntity.isAlive())
            submitResult(Result.FAIL);

        entity.getNavigator().tryMoveToEntityLiving(targetEntity,entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());

        if(entity.getDistanceSq(targetEntity)<2)
            submitResult(Result.SUCCESS);

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
