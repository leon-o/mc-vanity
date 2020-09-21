package top.leonx.vanity.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModContainerTypes;

import javax.annotation.Nonnull;

public class OutsiderInventoryContainer extends Container {
    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS, PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE,
            PlayerContainer.EMPTY_ARMOR_SLOT_HELMET};

    private final OutsiderEntity outsider;
    private final PlayerEntity player;
    private final int numCols            =9;
    private final int numRowsOutsiderInv = 4;
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};

    public OutsiderInventoryContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId,inv,readOutsiderFromBuffer(data));
    }

    public OutsiderInventoryContainer(int windowId, PlayerInventory inv, OutsiderEntity entity) {
        super(ModContainerTypes.OUTSIDER_INVENTORY.get(), windowId);
        this.outsider = entity;
        player = inv.player;

        int rowIndex=0,colIndex=0;

        for (int i = 0; i < outsider.inventory.mainInventory.size(); i++) {
            addSlot(new Slot(outsider.inventory,i,8+colIndex*18,18+rowIndex*18));
            colIndex++;
            if(colIndex== numCols)
            {
                colIndex=0;
                rowIndex++;
            }
        }
        for(int k = 0; k < 4; ++k) {
            final EquipmentSlotType equipmentslottype = VALID_EQUIPMENT_SLOTS[k];
            this.addSlot(new Slot(outsider.inventory, 39 - k, -15, 18 + k * 18) {
                /**
                 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in
                 * the case of armor slots)
                 */
                public int getSlotStackLimit() {
                    return 1;
                }

                /**
                 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
                 */
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return stack.canEquip(equipmentslottype, player);
                }

                /**
                 * Return whether this slot's stack can be taken from this slot.
                 */
                public boolean canTakeStack(@Nonnull PlayerEntity playerIn) {
                    ItemStack itemstack = this.getStack();
                    return (itemstack.isEmpty() || playerIn.isCreative() || !EnchantmentHelper.hasBindingCurse(itemstack)) && super.canTakeStack(playerIn);
                }

                @OnlyIn(Dist.CLIENT)
                public Pair<ResourceLocation, ResourceLocation> getBackground() {
                    return Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, ARMOR_SLOT_TEXTURES[equipmentslottype.getIndex()]);
                }
            });
        }
        addSlot(new Slot(outsider.inventory, 40, -15, 18 + 4 * 18+4));
        for(int l = 0; l < 3; ++l) {
            for(int j1 = 0; j1 < 9; ++j1) {
                this.addSlot(new Slot(inv, j1 + l * 9 + 9, 8 + j1 * 18, 103 + l * 18+5));
            }
        }

        for(int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(inv, i1, 8 + i1 * 18, 161+5));
        }


    }
    private static OutsiderEntity readOutsiderFromBuffer(PacketBuffer data)
    {
        if (data != null && Minecraft.getInstance().world != null) {
            Entity entityByID = Minecraft.getInstance().world.getEntityByID(data.readInt());
            if (entityByID instanceof OutsiderEntity) return  (OutsiderEntity) entityByID;
        }

        return null;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    /**
     * Handle when the stack in slot {@code index} is shift-clicked. Normally this moves the stack between the player
     * inventory and the other inventory(s).
     */
    @Nonnull
    public ItemStack transferStackInSlot(@Nonnull PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();
            if (index < this.numRowsOutsiderInv * 9) {
                if (!this.mergeItemStack(stackInSlot, this.numRowsOutsiderInv * 9, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(stackInSlot, 0, this.numRowsOutsiderInv * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}
