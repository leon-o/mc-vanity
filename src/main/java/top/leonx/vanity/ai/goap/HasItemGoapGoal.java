package top.leonx.vanity.ai.goap;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class HasItemGoapGoal extends GoapGoal{

    Predicate<ItemStack> itemStackPredicate;
    public HasItemGoapGoal(@Nonnull Predicate<ItemStack> itemStackPredicate) {
        super("item", e-> e.inventory.hasItemStack(itemStackPredicate));
        this.itemStackPredicate=itemStackPredicate;
    }
}
