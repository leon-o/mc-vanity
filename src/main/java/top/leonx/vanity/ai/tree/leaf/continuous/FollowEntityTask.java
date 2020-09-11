package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class FollowEntityTask extends BehaviorTreeTask<OutsiderEntity> {
    public Function<OutsiderEntity,LivingEntity> targetEntity;

    public FollowEntityTask(Function<OutsiderEntity,LivingEntity> targetEntityGetter) {
        this.targetEntity = targetEntityGetter;
    }

    private LivingEntity  followedEntity;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        followedEntity = targetEntity.apply(entity);
        if(followedEntity==null)
            submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(entity.getDistanceSq(followedEntity)<9)
            submitResult(Result.SUCCESS);
        else
            entity.getNavigator().tryMoveToEntityLiving(followedEntity,entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }
}
