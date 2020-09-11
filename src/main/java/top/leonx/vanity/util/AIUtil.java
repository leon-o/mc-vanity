package top.leonx.vanity.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
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
import top.leonx.vanity.entity.OutsiderEntity;

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

    public static Collection<ItemStack> getLivingEntityDrops(Entity entity) {
        if(!(entity instanceof LivingEntity)) return Collections.emptyList();
        LivingEntity livingEntity=(LivingEntity)entity;
        ResourceLocation resourcelocation = livingEntity.getLootTableResourceLocation();
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

    public static double sigmod(double x,double scale,double bias)
    {
        return 1/(1+Math.exp(-x*scale+bias));
    }

    public static double entityDangerousAssessment(LivingEntity target, LivingEntity owner)
    {
        return 0;
    }
    public static LivingEntity getMostDangerousEntityNear(OutsiderEntity entity)
    {
        Optional<List<LivingEntity>> visibleMobsOpt = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if (visibleMobsOpt.isPresent()) {
            List<LivingEntity>     visibleMobs   = visibleMobsOpt.get();
            Optional<LivingEntity> mostDangerous = visibleMobs.stream().max(Comparator.comparingDouble(mob -> AIUtil.entityDangerousAssessment(mob, entity)));
            return mostDangerous.orElse(null);
        }
        return null;
    }

    public static LivingEntity getClosestFoodProvider(OutsiderEntity entity)
    {
        Optional<List<LivingEntity>> visibleMobsOpt = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if (visibleMobsOpt.isPresent())
        {
            Optional<LivingEntity> closestFoodProvider = visibleMobsOpt.get().stream().filter(mob -> AIUtil.getLivingEntityDrops(mob).stream().anyMatch(ItemStack::isFood)).min(
                    Comparator.comparingDouble(mob -> mob.getDistanceSq(entity)));
            return closestFoodProvider.orElse(null);
        }
        return null;
    }
}
