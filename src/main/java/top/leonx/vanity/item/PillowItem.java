package top.leonx.vanity.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.client.event.sound.SoundEvent;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModBlocks;

public class PillowItem extends BlockItem {
    public PillowItem() {
        super(ModBlocks.PILLOW, new Item.Properties().group(ItemGroup.DECORATIONS));
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, PlayerEntity playerIn, LivingEntity target, Hand hand) {
        if(!(target instanceof OutsiderEntity)) return false;

        CompoundNBT owner = stack.getOrCreateChildTag("owner");
        owner.putUniqueId("uuid",target.getUniqueID());
        playerIn.world.playSound(null, target.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.BLOCKS,1f,1f);

        return true;
    }
}
