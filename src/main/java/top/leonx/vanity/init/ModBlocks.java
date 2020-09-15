package top.leonx.vanity.init;

import net.minecraft.block.Block;
import top.leonx.vanity.block.PillowBlock;
import top.leonx.vanity.block.VanityMirrorBlock;

public class ModBlocks {
    public static final Block VANITY_MIRROR=new VanityMirrorBlock().setRegistryName("vanity_mirror");
    public static final Block PILLOW=new PillowBlock().setRegistryName("pillow");
    public static final Block[] BLOCKS={VANITY_MIRROR,PILLOW};
}
