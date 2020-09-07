package top.leonx.vanity.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

import java.util.*;
import java.util.stream.Collectors;

public class AIUtil {
    static Random random = new Random();
    public static final BlockState DUMMY_BLOCK_STATE=new BlockState(Blocks.IRON_BLOCK, ImmutableMap.of()); // don't crash.
    public static final Comparator<ItemStack> COMPARE_VALUE=Comparator.comparingInt(AIUtil::getItemValue);
    public static final Comparator<ItemStack> COMPARE_HARDNESS=Comparator.comparingDouble(t->((BlockItem) t.getItem()).getBlock().getHarvestLevel(DUMMY_BLOCK_STATE));

    @SuppressWarnings("deprecation")
    public static int getItemValue(ItemStack itemStack) {
        Material material = ((BlockItem) itemStack.getItem()).getBlock().getMaterial(DUMMY_BLOCK_STATE);
        if(material.equals(Material.IRON))
            return 10;
        else if(material.equals(Material.SPONGE))
            return 9;
        else if(ItemTags.LOGS.contains(itemStack.getItem()))
            return 6;
        else if(ItemTags.WOOL.contains(itemStack.getItem()))
            return 4;
        else if(ItemTags.PLANKS.contains(itemStack.getItem()))
            return 4;
        else if(material.equals(Material.GLASS))
            return 5;
        else if(itemStack.getItem().equals(Items.COBBLESTONE))
            return 0;
        else if(material.equals(Material.ROCK))
            return 2;
        else if(material.equals(Material.CLAY))
            return 3;
        else if(itemStack.getItem().equals(Items.DIRT))
            return 0;
        return 5;
    }

    public static Collection<ItemStack> getLivingEntityDrops(LivingEntity entity) {
        ResourceLocation resourcelocation = entity.getLootTableResourceLocation();
        LootTable        loottable        = Objects.requireNonNull(entity.world.getServer()).getLootTableManager().getLootTableFromLocation(resourcelocation);

        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) entity.world)).withRandom(random).withParameter(LootParameters.POSITION, entity.getPosition()).withParameter(
                LootParameters.DAMAGE_SOURCE, DamageSource.GENERIC).withParameter(LootParameters.THIS_ENTITY, entity);
        lootcontext$builder = lootcontext$builder.withLuck(0);
        LootContext ctx = lootcontext$builder.build(LootParameterSets.ENTITY);

        return loottable.generate(ctx);
    }

    public List<IRecipe<?>> getItemRecipe(ServerWorld world,ItemStack itemStack)
    {
        RecipeManager manager = world.getServer().getRecipeManager();
        return manager.getRecipes().stream().filter(t -> t.getRecipeOutput().equals(itemStack)).collect(Collectors.toList());
    }
}
