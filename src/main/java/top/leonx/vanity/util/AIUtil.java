package top.leonx.vanity.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AIUtil {
    static Random random = new Random();
    public static final BlockState DUMMY_BLOCK_STATE=new BlockState(Blocks.IRON_BLOCK, ImmutableMap.of()); // don't crash.
    public static final Comparator<ItemStack> COMPARE_VALUE=Comparator.comparingDouble(AIUtil::getItemValue);
    public static final Comparator<ItemStack> COMPARE_HARDNESS=Comparator.comparingDouble(t->((BlockItem) t.getItem()).getBlock().getHarvestLevel(DUMMY_BLOCK_STATE));


    @SuppressWarnings("deprecation")
    public static double getItemValue(ItemStack itemStack) {
        Material material = null;
        if(itemStack.getItem() instanceof BlockItem)
        {
            material=((BlockItem) itemStack.getItem()).getBlock().getMaterial(DUMMY_BLOCK_STATE);
        }
        if(Objects.equals(material, Material.IRON))
            return 12;
        else if(Objects.equals(material, Material.SPONGE))
            return 9;
        else if(ItemTags.LOGS.contains(itemStack.getItem()))
            return 6;
        else if(ItemTags.WOOL.contains(itemStack.getItem()))
            return 4;
        else if(ItemTags.PLANKS.contains(itemStack.getItem()))
            return 1.5;
        else if(Objects.equals(material, Material.GLASS))
            return 2;
        else if(itemStack.getItem().equals(Items.COBBLESTONE))
            return 0;
        else if(Objects.equals(material, Material.ROCK))
            return 2;
        else if(Objects.equals(material, Material.CLAY))
            return 1;
        else if(itemStack.getItem().equals(Items.DIRT))
            return -1;
        return 0;
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

    public static List<ICraftingRecipe> getItemCraftingRecipe(ServerWorld world, ItemStack itemStack)
    {
        RecipeManager manager = world.getServer().getRecipeManager();
        return manager.getRecipes().stream().filter(
                t->t.getRecipeOutput().getItem().equals(itemStack.getItem()) && t instanceof ICraftingRecipe
        ).map(t->(ICraftingRecipe)t).collect(Collectors.toList());
        //return manager.getRecipes(IRecipeType.CRAFTING,craftingInventory,world).stream().filter(t->t.getRecipeOutput().equals(itemStack)).collect(Collectors.toList());
        //return manager.getRecipes().stream().filter(t -> t.getRecipeOutput().equals(itemStack)).collect(Collectors.toList());
    }

    public static double sigmod(double x,double scale,double bias)
    {
        return 1/(1+Math.exp(-x*scale+bias));
    }

    public static double entityDangerousAssessment(LivingEntity target, LivingEntity owner)
    {
        if(target==null || owner==null)return 0;
        IAttributeInstance attribute = target.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        //noinspection ConstantConditions
        if(attribute==null) return 0;

        float dangerous=0;

        if(target.getRevengeTarget()==owner || (target instanceof MobEntity && ((MobEntity)target).getAttackTarget()==owner))
            dangerous=1;

        return dangerous*sigmod(target.getDistance(owner), -0.4, -5) * attribute.getValue();
    }
    public static LivingEntity getMostDangerousEntityNear(OutsiderEntity entity)
    {
        Optional<List<LivingEntity>> visibleMobsOpt = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if (visibleMobsOpt.isPresent()) {
            List<LivingEntity>     visibleMobs   = visibleMobsOpt.get();
            Optional<LivingEntity> mostDangerous =
                    visibleMobs.stream().filter(t->!Objects.equals(t.getUniqueID(),entity.getFollowedPlayerUUID())).max(Comparator.comparingDouble(mob -> AIUtil.entityDangerousAssessment(mob,entity)));
            return mostDangerous.orElse(null);
        }
        return null;
    }

    public static LivingEntity getNearestFoodProvider(OutsiderEntity entity)
    {
        return getNearestItemProvider(entity, ItemStack::isFood);
    }

    public static LivingEntity getNearestItemProvider(OutsiderEntity entity, Predicate<ItemStack> predicate)
    {
        Optional<List<LivingEntity>> visibleMobsOpt = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
        if (visibleMobsOpt.isPresent())
        {
            Optional<LivingEntity> closestFoodProvider = visibleMobsOpt.get().stream().filter(mob -> AIUtil.getLivingEntityDrops(mob).stream().anyMatch(predicate)).min(
                    Comparator.comparingDouble(mob -> mob.getDistanceSq(entity)));
            return closestFoodProvider.orElse(null);
        }
        return null;
    }

    @SuppressWarnings("DuplicatedCode")
    public static double getModifiedAttackDamage(double baseValue, LivingEntity targetEntity, ItemStack stack)
    {
        double oBaseValue=baseValue;
        Multimap<String, AttributeModifier> attributeModifiers = stack.getItem().getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);
        Collection<AttributeModifier>       damageModifier = attributeModifiers.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
        for (AttributeModifier attributeModifier : damageModifier) {
            AttributeModifier.Operation operation = attributeModifier.getOperation();
            if(operation.equals(AttributeModifier.Operation.ADDITION))
            {
                baseValue+=attributeModifier.getAmount();
            }else if(operation.equals(AttributeModifier.Operation.MULTIPLY_BASE))
            {
                baseValue+=oBaseValue*attributeModifier.getAmount();
            }else {
                baseValue*=attributeModifier.getAmount();
            }
        }
        if(targetEntity!=null)
            baseValue+=EnchantmentHelper.getModifierForCreature(stack, targetEntity.getCreatureAttribute());

        return baseValue;
    }

    @SuppressWarnings("DuplicatedCode")
    public static double getModifiedAttackSpeed(double baseValue, ItemStack stack)
    {
        double oBaseValue=baseValue;
        Multimap<String, AttributeModifier> attributeModifiers = stack.getItem().getAttributeModifiers(EquipmentSlotType.MAINHAND, stack);
        Collection<AttributeModifier>       damageModifier = attributeModifiers.get(SharedMonsterAttributes.ATTACK_SPEED.getName());
        for (AttributeModifier attributeModifier : damageModifier) {
            AttributeModifier.Operation operation = attributeModifier.getOperation();
            if(operation.equals(AttributeModifier.Operation.ADDITION))
            {
                baseValue+=attributeModifier.getAmount();
            }else if(operation.equals(AttributeModifier.Operation.MULTIPLY_BASE))
            {
                baseValue+=oBaseValue*attributeModifier.getAmount();
            }else {
                baseValue*=attributeModifier.getAmount();
            }
        }

        return baseValue;
    }
}
