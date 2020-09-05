package top.leonx.vanity.entity.ai.brain.utilitybased.leaf;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.entity.ai.brain.utilitybased.UtilityBasedTask;

import java.util.Comparator;
import java.util.Optional;

public class DefendWithShieldTask extends UtilityBasedTask<OutsiderEntity> {
    public DefendWithShieldTask() {
        super((w,e,t)->{
            Optional<LivingEntity> hurtBy = e.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
            if(hurtBy.isPresent())
                return 1d-e.getHealth()/e.getMaxHealth();

            return 0d;
        });
    }

    @Override
    public void action(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        Optional<LivingEntity> hurtBy = entity.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
        hurtBy.ifPresent(t->{
            entity.getLookController().setLookPositionWithEntity(t,20,20);
            if(entity.inventory.findAndHeld(Hand.OFF_HAND, i -> i.getItem().equals(Items.SHIELD),
                                            Comparator.comparingDouble(p -> 1 - p.getSecond().getDamage() / (float) p.getSecond().getMaxDamage())))
                entity.setActiveHand(Hand.OFF_HAND);
        });
    }

    @Override
    public void ending(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(entity.getActiveHand()==Hand.OFF_HAND)
            entity.stopActiveHand();
    }
}
