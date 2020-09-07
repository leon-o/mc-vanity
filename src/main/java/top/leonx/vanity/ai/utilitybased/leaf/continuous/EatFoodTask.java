package top.leonx.vanity.ai.utilitybased.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;

public class EatFoodTask extends UtilityBasedTask<OutsiderEntity> {


    ItemStack eatingFood;
    int countOnStartUse;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        @SuppressWarnings("ConstantConditions") boolean isHeld = entity.inventory.findAndHeld(Hand.MAIN_HAND, ItemStack::isFood, Comparator.comparingDouble(t -> t.getSecond().getItem().getFood().getHealing()));
        if(isHeld)
        {
            eatingFood=entity.getHeldItem(Hand.MAIN_HAND);
            countOnStartUse=eatingFood.getCount();
            entity.setActiveHand(Hand.MAIN_HAND);
        }

        submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(!entity.isHandActive())
        {
            if(eatingFood.isEmpty() || eatingFood.getCount()<countOnStartUse)
                submitResult(Result.SUCCESS);
            else
                submitResult(Result.FAIL);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        entity.stopActiveHand();
    }
}
