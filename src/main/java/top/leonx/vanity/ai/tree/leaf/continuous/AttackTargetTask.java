package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class AttackTargetTask extends BehaviorTreeTask<OutsiderEntity> {
    Function<OutsiderEntity, LivingEntity> attackTargetSelector;

    public AttackTargetTask(Function<OutsiderEntity, LivingEntity> attackTargetSelector) {
        this.attackTargetSelector = attackTargetSelector;
    }

    @Override
    public void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity target = entity.getAttackTarget();
        if (target == null) return;

        if (!target.isAlive()) entity.setAttackTarget(null);
    }

    @Override
    public void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity attackTarget = selectAttackTarget(entity);

        if (attackTarget == null)
            submitResult(Result.FAIL);
    }

    @Override
    public void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity target = entity.getAttackTarget();
        if (target == null) return;

        entity.getNavigator().tryMoveToEntityLiving(target, entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        if (entity.canAttack(target)) entity.attackLootAt();

        if (!entity.getAttackTarget().isAlive()) {
            submitResult(Result.SUCCESS);
        }
    }

    public LivingEntity selectAttackTarget(OutsiderEntity entity) {
        return attackTargetSelector.apply(entity);
    }
}
