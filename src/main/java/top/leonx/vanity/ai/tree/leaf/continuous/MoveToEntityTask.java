package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.util.TernaryFunc;

public class MoveToEntityTask<T extends MobEntity> extends BehaviorTreeTask<T> {
    public LivingEntity targetEntity;

    public MoveToEntityTask(LivingEntity targetEntity) {
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
