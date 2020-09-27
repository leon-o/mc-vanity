package top.leonx.vanity.ai.goap;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

public class GroundItemGoapGoal extends GoapGoal {
    Predicate<ItemStack> itemStackPredicate;
    public GroundItemGoapGoal(@Nonnull Predicate<ItemStack> predicate) {
        super("ground_item", entity -> {
            List<ItemEntity> itemEntityList = entity.world.getEntitiesWithinAABB(EntityType.ITEM, entity.getBoundingBox().expand(10, 10, 10), t -> predicate.test(t.getItem()));
            return itemEntityList.size()>0;
        });
        this.itemStackPredicate=predicate;
    }
}
