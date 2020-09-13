package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;

public class EatFoodTask extends BehaviorTreeTask<OutsiderEntity> {


    ItemStack eatingFood;
    int countOnStartUse;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {

        boolean isHeld = entity.inventory.findAndHeld(Hand.MAIN_HAND, ItemStack::isFood, t -> t.getItem().getFood().getHealing());
        if(isHeld)
        {
            eatingFood=entity.getHeldItem(Hand.MAIN_HAND);
            countOnStartUse=eatingFood.getCount();
            entity.setActiveHand(Hand.MAIN_HAND);
            return;
        }

        submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(!entity.isHandActive())
        {
            if(eatingFood!=null && (eatingFood.isEmpty() || eatingFood.getCount()<countOnStartUse))
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
