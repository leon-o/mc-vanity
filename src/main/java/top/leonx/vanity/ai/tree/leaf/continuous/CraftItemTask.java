package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModPointOfInterest;
import top.leonx.vanity.util.AIUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CraftItemTask extends BehaviorTreeTask<OutsiderEntity> {
    Function<OutsiderEntity, ItemStack> itemGetter;

    public CraftItemTask(Function<OutsiderEntity, ItemStack> itemGetter,boolean full) {
        this.itemGetter = itemGetter;this.full=full;
    }
    private static final RecipeItemHelper recipeItemHelper =new RecipeItemHelper();
    private final        boolean          full;
    private              boolean          needCraftTable;
    private BlockPos closestCraftTable;
    private List<ICraftingRecipe> matchedRecipes;
    private ItemStack requestItemStack;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        recipeItemHelper.clear();
        entity.inventory.accountStacks(recipeItemHelper);

        requestItemStack  = itemGetter.apply(entity);
        List<ICraftingRecipe> itemRecipe = AIUtil.getItemCraftingRecipe(world, requestItemStack);
        int maxCraftable = itemRecipe.stream().map(t->recipeItemHelper.getBiggestCraftableStack(t,null)).max(Integer::compareTo).orElse(0);

        if(full && maxCraftable< requestItemStack.getCount())
        {
            submitResult(Result.FAIL);
            return;
        }
        matchedRecipes = itemRecipe.stream().filter(t->recipeItemHelper.canCraft(t,null)).collect(Collectors.toList());
        //matchedRecipes = itemRecipe.stream().filter(t -> t.getIngredients().stream().allMatch(entity.inventory::hasItemStack)).collect(Collectors.toList());

        if(matchedRecipes.size()==0)
        {
            submitResult(Result.FAIL);
            return;
        }
        needCraftTable = matchedRecipes.stream().noneMatch(t->t.canFit(2, 2));
        if(!needCraftTable) return;
        Optional<BlockPos> closest = world.getPointOfInterestManager().findClosest(ModPointOfInterest.CRAFT_TABLE.getPredicate(), t -> true, new BlockPos(entity), 48,
                                                                                   PointOfInterestManager.Status.ANY);
        if(closest.isPresent())
            closestCraftTable=closest.get();
        else{
            submitResult(Result.FAIL);
        }
        //System.out.println(closest.orElse(new BlockPos(0,0,0)));
    }
    private boolean startCrafting=false;
    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(!needCraftTable)
        {
            craft(entity);
        }else{
            Vec3d  craftTablePosVec = new Vec3d(closestCraftTable).add(0.5,0,0.5);
            double distToCraftTable = Math.sqrt(entity.getDistanceSq(craftTablePosVec));
            if(distToCraftTable>entity.getBlockReachDistance())
            {
                entity.getLookController().setLookPosition(craftTablePosVec);
                entity.getNavigator().tryMoveToXYZ(craftTablePosVec.x,craftTablePosVec.y,craftTablePosVec.z, (int) Math.min(1, distToCraftTable*0.4));
            }else{
                if(!startCrafting){
                    entity.swingArm(Hand.MAIN_HAND);
                    startCrafting=true;
                    delay(20);
                }

                craft(entity);
            }
        }
    }
    private void craft(OutsiderEntity entity)
    {
        ICraftingRecipe recipe = matchedRecipes.get(0);
        ItemStack recipeOutput = recipe.getRecipeOutput().copy(); //must copy
        List<ItemStack> consumedItemTmp=new ArrayList<>();
        if(recipe.getIngredients().stream().allMatch(r->{
            if (r.getMatchingStacks().length==0) {
                return true;
            }
            ItemStack itemStack = entity.inventory.findItemStack(r, Comparator.comparingDouble(ItemStack::getDamage));
            if(itemStack.isEmpty())
                return false;
            ItemStack copy = itemStack.copy();
            copy.setCount(1);
            consumedItemTmp.add(copy);
            return entity.inventory.decrStackSize(itemStack, 1);
        }))
        {
            requestItemStack.shrink(recipeOutput.getCount());
            entity.inventory.storeItemStack(recipeOutput);
            if(requestItemStack.isEmpty())
            {
                submitResult(Result.SUCCESS);
            }
        }else{
            consumedItemTmp.forEach(entity.inventory::storeItemStack); //if fail, return items.
            submitResult(Result.FAIL);
        }
    }
    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        startCrafting=false;
    }
}
