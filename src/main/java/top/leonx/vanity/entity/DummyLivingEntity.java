package top.leonx.vanity.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;

public class DummyLivingEntity extends LivingEntity {
    public DummyLivingEntity(EntityType<DummyLivingEntity> type, World worldIn) {
        super(type, worldIn);
    }


    @Override
    public void tick() {
    }

    @Override
    public void livingTick() {
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ImmutableList.of(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {

    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }
}
