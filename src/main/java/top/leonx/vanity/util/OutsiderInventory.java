package top.leonx.vanity.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tags.Tag;
import net.minecraft.util.Hand;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.antlr.v4.runtime.misc.Triple;
import top.leonx.vanity.entity.OutsiderEntity;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class OutsiderInventory implements IInventory, INameable {
    public final  NonNullList<ItemStack>       mainInventory     = NonNullList.withSize(36, ItemStack.EMPTY);
    public final  NonNullList<ItemStack>       armorInventory    = NonNullList.withSize(4, ItemStack.EMPTY);
    public final  NonNullList<ItemStack>       offHandInventory  = NonNullList.withSize(1, ItemStack.EMPTY);
    public final  int                          mainHandSlotIndex = 0;
    public final  OutsiderEntity               entity;
    private final List<NonNullList<ItemStack>> allInventories    = ImmutableList.of(this.mainInventory, this.armorInventory, this.offHandInventory);
    private       ItemStack                    itemStack         = ItemStack.EMPTY;
    private       int                          timesChanged;

    public OutsiderInventory(OutsiderEntity entity) {
        this.entity = entity;
    }

    /**
     * Get the size of the entity hotbar inventory
     */
//    public static int getHotbarSize() {
//        return 9;
//    }

//    public static boolean isHotbar(int index) {
//        return index >= 0 && index < 9;
//    }
    public void accountStacks(RecipeItemHelper recipeItemHelper) {
        for (ItemStack itemstack : this.mainInventory) {
            recipeItemHelper.accountPlainStack(itemstack);
        }

    }

    /**
     * Adds the stack to the specified slot in the entity's inventory. Returns {@code false} if it's not possible to
     * place the entire stack in the inventory.
     */
    public boolean add(int slotIn, ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        } else {
            try {
                if (stack.isDamaged()) {
                    if (slotIn == -1) {
                        slotIn = this.getFirstEmptyStack();
                    }

                    if (slotIn >= 0) {
                        this.mainInventory.set(slotIn, stack.copy());
                        this.mainInventory.get(slotIn).setAnimationsToGo(5);
                        stack.setCount(0);
                        return true;
                    } else if (this.entity.getAbilities().isCreativeMode) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = stack.getCount();
                        if (slotIn == -1) {
                            stack.setCount(this.storePartialItemStack(stack));
                        } else {
                            stack.setCount(this.addResource(slotIn, stack));
                        }

                    } while (!stack.isEmpty() && stack.getCount() < i);

                    if (stack.getCount() == i && this.entity.getAbilities().isCreativeMode) {
                        stack.setCount(0);
                        return true;
                    } else {
                        return stack.getCount() < i;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport         crashreport         = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addDetail("Registry Name", () -> String.valueOf(stack.getItem().getRegistryName()));
                crashreportcategory.addDetail("Item Class", () -> stack.getItem().getClass().getName());
                crashreportcategory.addDetail("Item ID", Item.getIdFromItem(stack.getItem()));
                crashreportcategory.addDetail("Item data", stack.getDamage());
                crashreportcategory.addDetail("Item name", () -> stack.getDisplayName().getString());
                throw new ReportedException(crashreport);
            }
        }
    }

    /**
     * Adds the stack to the first empty slot in the entity's inventory. Returns {@code false} if it's not possible to
     * place the entire stack in the inventory.
     */
    public boolean addItemStackToInventory(ItemStack itemStackIn) {
        return this.add(-1, itemStackIn);
    }

    /**
     * returns a entity armor item (as itemstack) contained in specified armor slot.
     */
    @OnlyIn(Dist.CLIENT)
    public ItemStack armorItemInSlot(int slotIn) {
        return this.armorInventory.get(slotIn);
    }

    public boolean canHarvestBlock(BlockState state) {
        return this.getStackInSlot(this.mainHandSlotIndex).canHarvestBlock(state);
    }

    public void clear() {
        for (List<ItemStack> list : this.allInventories) {
            list.clear();
        }

    }

    public int clearMatchingItems(Predicate<ItemStack> p_195408_1_, int count) {
        int i = 0;

        for (int j = 0; j < this.getSizeInventory(); ++j) {
            ItemStack itemstack = this.getStackInSlot(j);
            if (!itemstack.isEmpty() && p_195408_1_.test(itemstack)) {
                int k = count <= 0 ? itemstack.getCount() : Math.min(count - i, itemstack.getCount());
                i += k;
                if (count != 0) {
                    itemstack.shrink(k);
                    if (itemstack.isEmpty()) {
                        this.setInventorySlotContents(j, ItemStack.EMPTY);
                    }

                    if (count > 0 && i >= count) {
                        return i;
                    }
                }
            }
        }

        if (!this.itemStack.isEmpty() && p_195408_1_.test(this.itemStack)) {
            int l = count <= 0 ? this.itemStack.getCount() : Math.min(count - i, this.itemStack.getCount());
            i += l;
            if (count != 0) {
                this.itemStack.shrink(l);
                if (this.itemStack.isEmpty()) {
                    this.itemStack = ItemStack.EMPTY;
                }

                if (count > 0 && i >= count) {
                    return i;
                }
            }
        }

        return i;
    }

    /**
     * Copy the ItemStack contents from another Inventoryentity instance
     */
    public void copyInventory(OutsiderInventory entityInventory) {
        for (int i = 0; i < this.getSizeInventory(); ++i) {
            this.setInventorySlotContents(i, entityInventory.getStackInSlot(i));
        }
    }

    /**
     * Damages armor in each slot by the specified amount.
     */
    public void damageArmor(float damage) {
        if (!(damage <= 0.0F)) {
            damage = damage / 4.0F;
            if (damage < 1.0F) {
                damage = 1.0F;
            }

            for (int i = 0; i < this.armorInventory.size(); ++i) {
                ItemStack itemstack = this.armorInventory.get(i);
                if (itemstack.getItem() instanceof ArmorItem) {
                    int j = i;
                    itemstack.damageItem((int) damage, this.entity, (t) -> t.sendBreakAnimation(EquipmentSlotType.fromSlotTypeAndIndex(EquipmentSlotType.Group.ARMOR, j)));
                }
            }

        }
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Nonnull
    public ItemStack decrStackSize(int index, int count) {
        List<ItemStack> list = null;

        for (NonNullList<ItemStack> nonNullList : this.allInventories) {
            if (index < nonNullList.size()) {
                list = nonNullList;
                break;
            }

            index -= nonNullList.size();
        }

        return list != null && !list.get(index).isEmpty() ? ItemStackHelper.getAndSplit(list, index, count) : ItemStack.EMPTY;
    }

    public void deleteStack(ItemStack stack) {
        for (NonNullList<ItemStack> nonnulllist : this.allInventories) {
            for (int i = 0; i < nonnulllist.size(); ++i) {
                if (nonnulllist.get(i) == stack) {
                    nonnulllist.set(i, ItemStack.EMPTY);
                    break;
                }
            }
        }

    }
    public void deleteStackIf(Predicate<ItemStack> predicate) {
        this.allInventories.stream().flatMap(Collection::stream).filter(predicate).forEach(t -> t.setCount(0));
    }

    public boolean decrStackSize(Predicate<ItemStack> predicate, int totalCount) {
        int             remaining  = totalCount;
        List<ItemStack> itemStacks = this.allInventories.stream().flatMap(Collection::stream).filter(predicate).collect(Collectors.toList());
        for (ItemStack stack : itemStacks) {
            int shrinkCount = Math.min(stack.getCount(), remaining);
            stack.shrink(shrinkCount);
            remaining -= shrinkCount;
            if (remaining == 0) break;
        }
        return remaining == 0; //remaining equals 0 means itemStacks in inventory are enough.
    }

    public boolean decrStackSize(ItemStack itemStack, int totalCount) {
        return decrStackSize(itemStack::equals, totalCount);
    }

    /**
     * Drop all armor and main inventory items.
     */
    public void dropAllItems() {
        for (List<ItemStack> list : this.allInventories) {
            for (int i = 0; i < list.size(); ++i) {
                ItemStack itemstack = list.get(i);
                if (!itemstack.isEmpty()) {
                    this.entity.dropItem(itemstack, true, false);
                    list.set(i, ItemStack.EMPTY);
                }
            }
        }

    }

    public boolean findAndHeld(Hand hand, Predicate<ItemStack> predicate, ToDoubleFunction<ItemStack> stackToDoubleFunction) {

        return findAndHeld(hand, predicate, Comparator.comparingDouble(t -> stackToDoubleFunction.applyAsDouble(t.c)));
    }

    public boolean findAndHeld(Hand hand, Predicate<ItemStack> predicate, Comparator<Triple<NonNullList<ItemStack>, Integer, ItemStack>> comparator) {

        List<Triple<NonNullList<ItemStack>, Integer, ItemStack>> matched = new ArrayList<>();
        for (NonNullList<ItemStack> inventory : allInventories) {
            for (int i = 0; i < inventory.size(); i++) {
                if (predicate.test(inventory.get(i))) matched.add(new Triple<>(inventory, i, inventory.get(i)));
            }
        }

        Optional<Triple<NonNullList<ItemStack>, Integer, ItemStack>> max = matched.stream().max(comparator);
        if (max.isPresent()) {
            Triple<NonNullList<ItemStack>, Integer, ItemStack> triple = max.get();
            int                                                index  = triple.b;
            if (hand == Hand.MAIN_HAND) pickItemInMainHand(triple.a, index);
            else {
                pickItemInOffHand(triple.a, index);
            }
            return true;
        } else return false;
    }

    public ItemStack findItemStack(Predicate<ItemStack> predicate, Comparator<ItemStack> comparator) {
        Optional<ItemStack> max = allInventories.stream().flatMap(Collection::stream).filter(predicate).max(comparator);
        return max.orElse(ItemStack.EMPTY);
    }

    public int findSlotMatchingUnusedItem(ItemStack p_194014_1_) {
        for (int i = 0; i < this.mainInventory.size(); ++i) {
            ItemStack itemstack = this.mainInventory.get(i);
            if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(p_194014_1_, this.mainInventory.get(i)) && !this.mainInventory.get(
                    i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasDisplayName()) {
                return i;
            }
        }

        return -1;
    }

    public int getBestHotbarSlot() {
        for (int i = 0; i < 9; ++i) {
            int j = (this.mainHandSlotIndex + i) % 9;
            if (this.mainInventory.get(j).isEmpty()) {
                return j;
            }
        }

        for (int k = 0; k < 9; ++k) {
            int l = (this.mainHandSlotIndex + k) % 9;
            if (!this.mainInventory.get(l).isEnchanted()) {
                return l;
            }
        }

        return this.mainHandSlotIndex;
    }

    public float getDestroySpeed(BlockState state) {
        return this.mainInventory.get(this.mainHandSlotIndex).getDestroySpeed(state);
    }

    /**
     * Returns the first item stack that is empty.
     */
    public int getFirstEmptyStack() {
        for (int i = 0; i < this.mainInventory.size(); ++i) {
            if (this.mainInventory.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Stack helds by mouse, used in GUI and Containers
     */
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    /**
     * Set the stack helds by mouse, used in GUI/Container
     */
    public void setItemStack(ItemStack itemStackIn) {
        this.itemStack = itemStackIn;
    }

    /**
     * Returns the item stack currently held by the entity.
     */
    public ItemStack getMainHandSlotIndex() {
        return this.mainInventory.get(this.mainHandSlotIndex);
    }

    @Nonnull
    public ITextComponent getName() {
        return new TranslationTextComponent("container.inventory");
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return this.mainInventory.size() + this.armorInventory.size() + this.offHandInventory.size();
    }

    /**
     * Finds the stack or an equivalent one in the main inventory
     */
    @OnlyIn(Dist.CLIENT)
    public int getSlotFor(ItemStack stack) {
        for (int i = 0; i < this.mainInventory.size(); ++i) {
            if (!this.mainInventory.get(i).isEmpty() && this.stackEqualExact(stack, this.mainInventory.get(i))) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Nonnull
    public ItemStack getStackInSlot(int index) {
        List<ItemStack> list = null;

        for (NonNullList<ItemStack> nonNullList : this.allInventories) {
            if (index < nonNullList.size()) {
                list = nonNullList;
                break;
            }

            index -= nonNullList.size();
        }

        return list == null ? ItemStack.EMPTY : list.get(index);
    }

    /**
     * Stores a stack in the entity's inventory. It first tries to place it in the selected slot in the entity's hotbar,
     * then the offhand slot, then any available/empty slot in the entity's inventory.
     */
    public int getStorableIndex(ItemStack itemStackIn) {
        if (this.canMergeStacks(this.getStackInSlot(this.mainHandSlotIndex), itemStackIn)) {
            return this.mainHandSlotIndex;
        } else if (this.canMergeStacks(this.getStackInSlot(40), itemStackIn)) {
            return 40;
        } else {
            for (int i = 0; i < this.mainInventory.size(); ++i) {
                if (this.canMergeStacks(this.mainInventory.get(i), itemStackIn)) {
                    return i;
                }
            }
            return -1;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getTimesChanged() {
        return this.timesChanged;
    }

    /**
     * Returns true if the specified ItemStack exists in the inventory.
     */
    public boolean hasItemStack(ItemStack itemStackIn) {
        label23:
        for (List<ItemStack> list : this.allInventories) {
            Iterator<ItemStack> iterator = list.iterator();

            while (true) {
                if (!iterator.hasNext()) {
                    continue label23;
                }

                ItemStack itemstack = iterator.next();
                if (!itemstack.isEmpty() && itemstack.isItemEqual(itemStackIn)) {
                    break;
                }
            }

            return true;
        }

        return false;
    }

    public boolean hasItemStack(Predicate<ItemStack> predicate) {
        return this.allInventories.stream().flatMap(Collection::stream).anyMatch(predicate);
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasTag(Tag<Item> itemTag) {
        label23:
        for (List<ItemStack> list : this.allInventories) {
            Iterator<ItemStack> iterator = list.iterator();

            while (true) {
                if (!iterator.hasNext()) {
                    continue label23;
                }

                ItemStack itemstack = iterator.next();
                if (!itemstack.isEmpty() && itemTag.contains(itemstack.getItem())) {
                    break;
                }
            }

            return true;
        }

        return false;
    }

    public boolean isEmpty() {
        for (ItemStack itemstack : this.mainInventory) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemStack1 : this.armorInventory) {
            if (!itemStack1.isEmpty()) {
                return false;
            }
        }

        for (ItemStack itemStack2 : this.offHandInventory) {
            if (!itemStack2.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isUsableByPlayer(@Nonnull PlayerEntity player) {
        return false;
    }

    /**
     * Don't rename this method to canInteractWith due to conflicts with Container
     */
    public boolean isUsableByEntity(OutsiderEntity entity) {
        if (!this.entity.isAlive()) {
            return false;
        } else {
            return !(entity.getDistanceSq(this.entity) > 64.0D);
        }
    }

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    public void markDirty() {
        ++this.timesChanged;
    }

    public void pickItemInMainHand(NonNullList<ItemStack> inventory, int index) {
        //this.mainHandSlotIndex = this.getBestHotbarSlot();
        ItemStack itemstack = this.mainInventory.get(this.mainHandSlotIndex);
        this.mainInventory.set(this.mainHandSlotIndex, inventory.get(index));
        inventory.set(index, itemstack);
    }

    public void pickItemInOffHand(NonNullList<ItemStack> inventory, int index) {
        ItemStack itemstack = this.offHandInventory.get(0);
        this.offHandInventory.set(0, inventory.get(index));
        inventory.set(index, itemstack);
    }

    public void placeItemBackInInventory(World worldIn, ItemStack stack) {
        if (!worldIn.isRemote) {
            while (!stack.isEmpty()) {
                int i = this.getStorableIndex(stack);
                if (i == -1) {
                    i = this.getFirstEmptyStack();
                }

                if (i == -1) {
                    this.entity.dropItem(stack, false, false);
                    break;
                }

                int j = stack.getMaxStackSize() - this.getStackInSlot(i).getCount();
                if (this.add(i, stack.split(j))) {
                    // TODO
                    //((ServerentityEntity)this.entity).connection.sendPacket(new SSetSlotPacket(-2, i, this.getStackInSlot(i)));
                }
            }

        }
    }



    /**
     * Reads from the given tag list and fills the slots in the inventory with the correct items.
     */
    public void read(ListNBT nbtTagListIn) {
        this.mainInventory.clear();
        this.armorInventory.clear();
        this.offHandInventory.clear();

        for (int i = 0; i < nbtTagListIn.size(); ++i) {
            CompoundNBT compoundnbt = nbtTagListIn.getCompound(i);
            int         j           = compoundnbt.getByte("Slot") & 255;
            ItemStack   itemstack   = ItemStack.read(compoundnbt);
            if (!itemstack.isEmpty()) {
                if (j < this.mainInventory.size()) {
                    this.mainInventory.set(j, itemstack);
                } else if (j >= 100 && j < this.armorInventory.size() + 100) {
                    this.armorInventory.set(j - 100, itemstack);
                } else if (j >= 150 && j < this.offHandInventory.size() + 150) {
                    this.offHandInventory.set(j - 150, itemstack);
                }
            }
        }

    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Nonnull
    public ItemStack removeStackFromSlot(int index) {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonNullList : this.allInventories) {
            if (index < nonNullList.size()) {
                nonnulllist = nonNullList;
                break;
            }

            index -= nonNullList.size();
        }

        if (nonnulllist != null && !nonnulllist.get(index).isEmpty()) {
            ItemStack itemstack = nonnulllist.get(index);
            nonnulllist.set(index, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int index, @Nonnull ItemStack stack) {
        NonNullList<ItemStack> nonnulllist = null;

        for (NonNullList<ItemStack> nonNullList : this.allInventories) {
            if (index < nonNullList.size()) {
                nonnulllist = nonNullList;
                break;
            }

            index -= nonNullList.size();
        }

        if (nonnulllist != null) {
            nonnulllist.set(index, stack);
        }

    }

    /**
     * Merge itemStack if is mergeable or put it in first empty slot
     *
     * @param itemStack itemStack
     * @return return true when success
     */
    public boolean storeItemStack(ItemStack itemStack) {
        return add(getStorableIndex(itemStack), itemStack);
    }

    public boolean isCanStore(ItemStack itemStack) {
        int storableIndex = getStorableIndex(itemStack);
        if (storableIndex != -1) return true;
        else return getFirstEmptyStack() != -1; //返回-1表示没有空的栏位
    }

    /**
     * Decrement the number of animations remaining. Only called on client side. This is used to handle the animation of
     * receiving a block.
     */
    public void tick() {
        for (NonNullList<ItemStack> nonNullList : this.allInventories) {
            for (int i = 0; i < nonNullList.size(); ++i) {
                if (!nonNullList.get(i).isEmpty()) {
                    nonNullList.get(i).inventoryTick(this.entity.world, this.entity, i, this.mainHandSlotIndex == i);
                }
            }
        }
        //TODO
        //armorInventory.forEach(e -> e.onArmorTick(entity.world, entity));
    }

    /**
     * Writes the inventory out as a list of compound tags. This is where the slot indices are used (+100 for armor, +80
     * for crafting).
     */
    public ListNBT write(ListNBT nbtTagListIn) {
        for (int i = 0; i < this.mainInventory.size(); ++i) {
            if (!this.mainInventory.get(i).isEmpty()) {
                CompoundNBT compoundnbt = new CompoundNBT();
                compoundnbt.putByte("Slot", (byte) i);
                this.mainInventory.get(i).write(compoundnbt);
                nbtTagListIn.add(compoundnbt);
            }
        }

        for (int j = 0; j < this.armorInventory.size(); ++j) {
            if (!this.armorInventory.get(j).isEmpty()) {
                CompoundNBT compoundnbt1 = new CompoundNBT();
                compoundnbt1.putByte("Slot", (byte) (j + 100));
                this.armorInventory.get(j).write(compoundnbt1);
                nbtTagListIn.add(compoundnbt1);
            }
        }

        for (int k = 0; k < this.offHandInventory.size(); ++k) {
            if (!this.offHandInventory.get(k).isEmpty()) {
                CompoundNBT compoundnbt2 = new CompoundNBT();
                compoundnbt2.putByte("Slot", (byte) (k + 150));
                this.offHandInventory.get(k).write(compoundnbt2);
                nbtTagListIn.add(compoundnbt2);
            }
        }

        return nbtTagListIn;
    }

    private int addResource(int index, ItemStack itemStack) {
        //Item item = itemStack.getItem();
        int       i         = itemStack.getCount();
        ItemStack itemstack = this.getStackInSlot(index);
        if (itemstack.isEmpty()) {
            itemstack = itemStack.copy(); // Forge: Replace Item clone above to preserve item capabilities when picking the item up.
            itemstack.setCount(0);
            if (itemStack.hasTag()) {
                //noinspection ConstantConditions
                itemstack.setTag(itemStack.getTag().copy());
            }

            this.setInventorySlotContents(index, itemstack);
        }

        int j = i;
        if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > this.getInventoryStackLimit() - itemstack.getCount()) {
            j = this.getInventoryStackLimit() - itemstack.getCount();
        }

        if (j != 0) {
            i = i - j;
            itemstack.grow(j);
            itemstack.setAnimationsToGo(5);
        }
        return i;
    }

    private boolean canMergeStacks(ItemStack stack1, ItemStack stack2) {
        return !stack1.isEmpty() && this.stackEqualExact(stack1, stack2) && stack1.isStackable() && stack1.getCount() < stack1.getMaxStackSize() && stack1.getCount() < this.getInventoryStackLimit();
    }

    /**
     * Checks item, NBT, and meta if the item is not damageable
     */
    private boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /**
     * This function stores as many items of an ItemStack as possible in a matching slot and returns the quantity of left
     * over items.
     */
    private int storePartialItemStack(ItemStack itemStackIn) {
        int i = this.getStorableIndex(itemStackIn);
        if (i == -1) {
            i = this.getFirstEmptyStack();
        }

        return i == -1 ? itemStackIn.getCount() : this.addResource(i, itemStackIn);
    }
}
