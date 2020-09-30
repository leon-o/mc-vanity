package top.leonx.vanity.init;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.block.PillowBlock;
import top.leonx.vanity.block.VanityMirrorBlock;

public final class  ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, VanityMod.MOD_ID);

    public static final RegistryObject<Block> VANITY_MIRROR=BLOCKS.register("vanity_mirror",VanityMirrorBlock::new);
    public static final RegistryObject<Block> PILLOW=BLOCKS.register("pillow",PillowBlock::new);
//    public static final RegistryObject<Block>  VANITY_WHITE_BED = BLOCKS.register("vanity_white_bed", ()->new VanityBedBlock(DyeColor.WHITE,
//                                                                                                             Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_ORANGE_BED = BLOCKS.register("vanity_orange_bed", ()->new VanityBedBlock(DyeColor.ORANGE, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_MAGENTA_BED = BLOCKS.register("vanity_magenta_bed", ()->new VanityBedBlock(DyeColor.MAGENTA, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_LIGHT_BLUE_BED = BLOCKS.register("vanity_light_blue_bed", ()->new VanityBedBlock(DyeColor.LIGHT_BLUE, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_YELLOW_BED = BLOCKS.register("vanity_yellow_bed", ()->new VanityBedBlock(DyeColor.YELLOW, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_LIME_BED = BLOCKS.register("vanity_lime_bed", ()->new VanityBedBlock(DyeColor.LIME, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_PINK_BED = BLOCKS.register("vanity_pink_bed", ()->new VanityBedBlock(DyeColor.PINK, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_GRAY_BED = BLOCKS.register("vanity_gray_bed", ()->new VanityBedBlock(DyeColor.GRAY, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_LIGHT_GRAY_BED = BLOCKS.register("vanity_light_gray_bed", ()->new VanityBedBlock(DyeColor.LIGHT_GRAY, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_CYAN_BED = BLOCKS.register("vanity_cyan_bed", ()->new VanityBedBlock(DyeColor.CYAN, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_PURPLE_BED = BLOCKS.register("vanity_purple_bed", ()->new VanityBedBlock(DyeColor.PURPLE, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_BLUE_BED = BLOCKS.register("vanity_blue_bed", ()->new VanityBedBlock(DyeColor.BLUE, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_BROWN_BED = BLOCKS.register("vanity_brown_bed", ()->new VanityBedBlock(DyeColor.BROWN, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_GREEN_BED = BLOCKS.register("vanity_green_bed", ()->new VanityBedBlock(DyeColor.GREEN, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_RED_BED = BLOCKS.register("vanity_red_bed", ()->new VanityBedBlock(DyeColor.RED, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));
//    public static final RegistryObject<Block>  VANITY_BLACK_BED = BLOCKS.register("vanity_black_bed", ()->new VanityBedBlock(DyeColor.BLACK, Block.Properties.create(Material.WOOL).sound(SoundType.WOOD).hardnessAndResistance(0.2F).notSolid()));

//    public static Block[] getAllBedBlocks()
//    {
//        return new Block[]{VANITY_WHITE_BED.get(),VANITY_ORANGE_BED.get(),VANITY_MAGENTA_BED.get(),VANITY_LIGHT_BLUE_BED.get(),VANITY_YELLOW_BED.get(),
//                VANITY_LIME_BED.get(),VANITY_PINK_BED.get(),VANITY_GRAY_BED.get(),VANITY_LIGHT_GRAY_BED.get(),VANITY_CYAN_BED.get(),VANITY_PURPLE_BED.get(),
//        VANITY_BLUE_BED.get(),VANITY_BROWN_BED.get(),VANITY_GREEN_BED.get(),VANITY_RED_BED.get(),VANITY_BLACK_BED.get()};
//    }
}
