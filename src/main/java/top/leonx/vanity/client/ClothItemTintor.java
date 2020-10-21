package top.leonx.vanity.client;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.item.AbstractClothItem;

@OnlyIn(Dist.CLIENT)
public class ClothItemTintor implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        if(stack.getItem() instanceof AbstractClothItem && tintIndex==1)
        {
            return ((AbstractClothItem)stack.getItem()).getColor(stack);
        }
        return 0xFFFFFF;
    }
}
