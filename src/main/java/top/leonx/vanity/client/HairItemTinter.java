package top.leonx.vanity.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.item.AbstractHairItem;

@OnlyIn(Dist.CLIENT)
public class HairItemTinter implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof AbstractHairItem && tintIndex==1)
        {
            return ((AbstractHairItem)stack.getItem()).getColor(stack);
        }
        return 0xFFFFFF;
    }
}
