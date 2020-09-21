package top.leonx.vanity.init;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.block.PillowBlock;
import top.leonx.vanity.block.VanityMirrorBlock;

import java.util.Locale;

public final class  ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, VanityMod.MOD_ID);

    public static final RegistryObject<Block> VANITY_MIRROR=BLOCKS.register("vanity_mirror",VanityMirrorBlock::new);
    public static final RegistryObject<Block> PILLOW=BLOCKS.register("pillow",PillowBlock::new);
}
