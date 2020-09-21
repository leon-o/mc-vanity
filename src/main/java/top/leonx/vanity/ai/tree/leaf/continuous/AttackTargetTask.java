package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.util.AIUtil;

import java.util.function.Function;

public class AttackTargetTask extends BehaviorTreeTask<OutsiderEntity> {
    Function<OutsiderEntity, LivingEntity> attackTargetSelector;

    public AttackTargetTask(Function<OutsiderEntity, LivingEntity> attackTargetSelector) {
        this.attackTargetSelector = attackTargetSelector;
    }

    @Override
    public void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        entity.getNavigator().clearPath();
        entity.setAttackTarget(null);
    }

    @Override
    public void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity attackTarget = selectAttackTarget(entity);

        double damageBase = entity.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
        double speedBase=entity.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
        entity.inventory.findAndHeld(Hand.MAIN_HAND,t->true,itemStack-> AIUtil.getModifiedAttackDamage(damageBase,attackTarget,itemStack)*AIUtil.getModifiedAttackSpeed(speedBase,itemStack));

        entity.setAttackTarget(attackTarget);

        if (attackTarget == null)
            submitResult(Result.FAIL);
    }

    @Override
    public void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity target = entity.getAttackTarget();
        if (target == null) return;

        entity.getNavigator().tryMoveToEntityLiving(target, 1);
        entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        if (entity.canAttack(target) && entity.getAttackCoolingPercentage(1f)>=1) entity.interactionManager.attackLootAt();

        if (!entity.getAttackTarget().isAlive()) {
            submitResult(Result.SUCCESS);
        }
    }

    public LivingEntity selectAttackTarget(OutsiderEntity entity) {
        return attackTargetSelector.apply(entity);
    }
}
