package top.leonx.vanity.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class AIUtil {
    static Random random = new Random();

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
