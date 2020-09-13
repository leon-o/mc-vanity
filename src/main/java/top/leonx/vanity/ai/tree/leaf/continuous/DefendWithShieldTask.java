package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.UtilityBasedTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.Comparator;
import java.util.Optional;

public class DefendWithShieldTask extends BehaviorTreeTask<OutsiderEntity> {

    @Override
    public void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }

    @Override
    public void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        Optional<LivingEntity> hurtBy = entity.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
        hurtBy.ifPresent(t->{
            entity.getLookController().setLookPositionWithEntity(t,20,20);
            if(entity.inventory.findAndHeld(Hand.OFF_HAND, i -> i.getItem().equals(Items.SHIELD),
                                            p -> 1 - p.getDamage() / (float) p.getMaxDamage()))
                entity.setActiveHand(Hand.OFF_HAND);
        });
    }

    @Override
    public void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(entity.getActiveHand()==Hand.OFF_HAND)
            entity.stopActiveHand();
    }
}
