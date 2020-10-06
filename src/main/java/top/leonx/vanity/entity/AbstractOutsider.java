package top.leonx.vanity.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.INPC;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractOutsider extends AgeableEntity implements IHasFoodStats<AbstractOutsider>, IRangedAttackMob, INPC , IOutsider {


    protected AbstractOutsider(EntityType<? extends AgeableEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    public abstract GeneralFoodStats<AbstractOutsider> getFoodStats();

    public abstract PlayerAbilities getAbilities();

    @Nonnull
    public abstract NeedsStatus getNeedsStatus();

    @Nonnull
    public abstract OutsiderInventory getInventory();

    public abstract CooldownTracker getItemCooldownTracker();

    @Nullable
    public ItemEntity dropItem(ItemStack droppedItem, boolean dropAround, boolean traceItem) {
        if (droppedItem.isEmpty()) {
            return null;
        } else {
            double     d0         = this.getPosYEye() - (double) 0.3F;
            ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), d0, this.getPosZ(), droppedItem);
            itementity.setPickupDelay(40);
            if (traceItem) {
                itementity.setThrowerId(this.getUniqueID());
            }

            if (dropAround) {
                float f  = this.rand.nextFloat() * 0.5F;
                float f1 = this.rand.nextFloat() * ((float) Math.PI * 2F);
                itementity.setMotion(-MathHelper.sin(f1) * f, 0.2F, MathHelper.cos(f1) * f);
            } else {
                //float f7 = 0.3F;
                float f8 = MathHelper.sin(this.rotationPitch * ((float) Math.PI / 180F));
                float f2 = MathHelper.cos(this.rotationPitch * ((float) Math.PI / 180F));
                float f3 = MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F));
                float f4 = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F));
                float f5 = this.rand.nextFloat() * ((float) Math.PI * 2F);
                float f6 = 0.02F * this.rand.nextFloat();
                itementity.setMotion((double) (-f3 * f2 * 0.3F) + Math.cos(f5) * (double) f6, -f8 * 0.3F + 0.1F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F,
                                     (double) (f4 * f2 * 0.3F) + Math.sin(f5) * (double) f6);
            }

            return itementity;
        }
    }
    @Override
    public ItemStack getItemStackFromSlot(EquipmentSlotType slotIn) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            return this.getInventory().getMainHandSlotIndex();
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            return this.getInventory().offHandInventory.get(0);
        } else {
            return slotIn.getSlotType() == EquipmentSlotType.Group.ARMOR ? this.getInventory().armorInventory.get(slotIn.getIndex()) : ItemStack.EMPTY;
        }
    }

    public void EquipItem(EquipmentSlotType slotIn, ItemStack stack) {
        setItemStackToSlot(slotIn, stack.copy());
        stack.setCount(0);
    }

    @Override
    public ItemStack onFoodEaten(World world, ItemStack stack) {
        getFoodStats().consume(stack.getItem(), stack);
        return super.onFoodEaten(world, stack);
    }
    @Override
    public boolean shouldHeal() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    /**
     * Add the exhaustion of food Stats
     */
    @Override
    public void addExhaustion(float exhaustion) {
        if (!this.getAbilities().disableDamage) {
            if (!this.world.isRemote) {
                this.getFoodStats().addExhaustion(exhaustion);
            }
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        getFoodStats().write(compound);
        getNeedsStatus().write(compound);
        getAbilities().write(compound);
        compound.put("Inventory", this.getInventory().write(new ListNBT()));
    }
    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        getFoodStats().read(compound);
        getNeedsStatus().read(compound);
        getAbilities().read(compound);
        ListNBT listnbt = compound.getList("Inventory", 10);
        this.getInventory().read(listnbt);
    }

    public CompoundNBT getSerializedCaps()
    {
        return serializeCaps();
    }
    public void deserializeCapsProxy(CompoundNBT nbt)
    {
        this.deserializeNBT(nbt);
    }

    public abstract CooldownTracker getCooldownTracker();

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }
}
