package top.leonx.vanity.util;

import net.minecraft.client.audio.Sound;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;

import java.util.Collection;
import java.util.Random;

public class AIUtil {
    static Random random = new Random();

    //static LootContext ctx;
    public static Collection<ItemStack> getLivingEntityDrops(LivingEntity entity) {
        ResourceLocation resourcelocation = entity.getLootTableResourceLocation();
        LootTable        loottable        = entity.world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);

        LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld) entity.world)).withRandom(random).withParameter(LootParameters.POSITION, entity.getPosition()).withParameter(
                LootParameters.DAMAGE_SOURCE, DamageSource.GENERIC).withParameter(LootParameters.THIS_ENTITY, entity);
        lootcontext$builder = lootcontext$builder.withLuck(0);
        LootContext ctx = lootcontext$builder.build(LootParameterSets.ENTITY);

        return loottable.generate(ctx);
    }
}
