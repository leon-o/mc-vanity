package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModParticleTypes;
import top.leonx.vanity.init.ModPointOfInterest;
import top.leonx.vanity.util.AIUtil;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Predicate;

import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class CookTask extends BehaviorTreeTask<OutsiderEntity> {
    BlockPos furnacePos;
    Predicate<ItemStack> foodPredicate;
    ItemStack selectedFood;
    public CookTask(Predicate<ItemStack> foodPredicate) {
        this.foodPredicate = foodPredicate;
    }

    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        selectedFood = entity.inventory.findItemStack(foodPredicate,
                                                             Comparator.comparingInt(ItemStack::getCount));
        if(selectedFood.isEmpty())
        {
            submitResult(Result.FAIL);
            return;
        }

        Optional<BlockPos> closestSmoker = world.getPointOfInterestManager().findClosest(
                ModPointOfInterest.SMOKER.get().getPredicate(), t -> true, new BlockPos(entity), 48,
                PointOfInterestManager.Status.ANY);
        if(!closestSmoker.isPresent()){
            Optional<BlockPos> closestFurnace = world.getPointOfInterestManager().findClosest(
                    ModPointOfInterest.FURNACE.get().getPredicate(), t -> true, new BlockPos(entity), 48,
                    PointOfInterestManager.Status.ANY);
            closestFurnace.ifPresent(pos -> furnacePos = pos);
        }else{
            furnacePos=closestSmoker.get();
        }

        if(furnacePos==null || !furnacePos.withinDistance(entity.getPosition(),entity.getBlockReachDistance()))
            submitResult(Result.FAIL);
    }
    boolean openedContainer;
    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        AbstractFurnaceTileEntity furnaceTileEntity = (AbstractFurnaceTileEntity) world.getTileEntity(furnacePos);
        if (furnaceTileEntity != null) {
            boolean addFoodSuccessful = addFoodToFurnace(furnaceTileEntity);
            boolean addFuelSuccessful = addFuelToFurnace(entity, furnaceTileEntity);

            if(addFoodSuccessful && addFuelSuccessful)
            {
                submitResult(Result.SUCCESS);
            }else{
                submitResult(Result.FAIL);
            }
        }

    }

    private boolean addFoodToFurnace(AbstractFurnaceTileEntity furnaceTileEntity) {
        LazyOptional<IItemHandler> upCap = furnaceTileEntity.getCapability(ITEM_HANDLER_CAPABILITY,
                                                                           Direction.UP);

        if (upCap != null && upCap.isPresent()) {
            IItemHandler itemHandler = upCap.orElse(EmptyHandler.INSTANCE);
            ItemStack returnedItemStack = itemHandler.insertItem(0, selectedFood, true);
            selectedFood.setCount(returnedItemStack.getCount());

            return itemHandler.getStackInSlot(0).getCount()>0;
        }
        return false;
    }

    private boolean addFuelToFurnace(OutsiderEntity entity, AbstractFurnaceTileEntity furnaceTileEntity) {
        LazyOptional<IItemHandler> fuelCap = furnaceTileEntity.getCapability(ITEM_HANDLER_CAPABILITY,
                                                                             Direction.NORTH);
        if(fuelCap!=null && fuelCap.isPresent())
        {
            IItemHandler handler    = fuelCap.orElse(EmptyHandler.INSTANCE);
            ItemStack    fuelInSlot = handler.getStackInSlot(0);
            ItemStack fuelInInventory;
            if(fuelInSlot.getCount()>0)
            {
                fuelInInventory =
                        entity.inventory.findItemStack(t -> ItemHandlerHelper.canItemStacksStack(t,fuelInSlot),
                                                                           Comparator.comparingInt(ItemStack::getCount));
            }else{
                fuelInInventory = entity.inventory.findItemStack(AbstractFurnaceTileEntity::isFuel,
                                                                           Comparator.comparingDouble(
                                                                                   AIUtil::getItemValue).reversed());
            }
            ItemStack returnedItemStack = handler.insertItem(0, fuelInInventory, true);
            fuelInInventory.setCount(returnedItemStack.getCount());

            return handler.getStackInSlot(0).getCount() > 0;
        }

        return false;
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        furnacePos=null;
    }
}
