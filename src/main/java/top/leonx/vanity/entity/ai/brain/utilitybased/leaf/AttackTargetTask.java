package top.leonx.vanity.entity.ai.brain.utilitybased.leaf;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.entity.ai.brain.utilitybased.UtilityBasedTask;

public class AttackTargetTask extends UtilityBasedTask<OutsiderEntity> {
    public AttackTargetTask() {
        super((w,e,t)->{
            if(e.getAttackTarget()==null) return 0d;
            return (double) (e.getHealth() / e.getMaxHealth()); //生命越多越勇
        });
    }

    @Override
    public void action(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity target = entity.getAttackTarget();
        if(target ==null) return;

        entity.getNavigator().tryMoveToEntityLiving(target, entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        entity.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);
        if(entity.canAttack(target))
            entity.attackLootAt();
    }

    @Override
    public void ending(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        LivingEntity target = entity.getAttackTarget();
        if(target ==null) return;

        if(!target.isAlive())
            entity.setAttackTarget(null);
    }
}
