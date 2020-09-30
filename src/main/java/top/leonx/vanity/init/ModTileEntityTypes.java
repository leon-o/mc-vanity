package top.leonx.vanity.init;

import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.tileentity.PillowTileEntity;
import top.leonx.vanity.tileentity.VanityBedTileEntity;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, VanityMod.MOD_ID);

    public static final RegistryObject<TileEntityType<VanityMirrorTileEntity>> VANITY_MIRROR      = TILE_ENTITY_TYPES.register("vanity_mirror",
                                                                                                                          () -> TileEntityType.Builder.create(VanityMirrorTileEntity::new,ModBlocks.VANITY_MIRROR.get()).build(null));
    public static final RegistryObject<TileEntityType<VanityBedTileEntity>>    VANITY_BED      = TILE_ENTITY_TYPES.register("vanity_bed",()->TileEntityType.Builder.create(VanityBedTileEntity::new,
                                                                                                                                                                           Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED).build(null));


    public static final RegistryObject<TileEntityType<PillowTileEntity>>       PILLOW_TILE_ENTITY = TILE_ENTITY_TYPES.register("pillow", () ->
            TileEntityType.Builder.create(PillowTileEntity::new,ModBlocks.PILLOW.get()).build(null));
}
