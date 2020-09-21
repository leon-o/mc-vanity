package top.leonx.vanity.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.tileentity.PillowTileEntity;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, VanityMod.MOD_ID);

    public static final RegistryObject<TileEntityType<VanityMirrorTileEntity>> VANITY_MIRROR = TILE_ENTITY_TYPES.register("vanity_mirror",
                                                                                                                          () -> TileEntityType.Builder.create(VanityMirrorTileEntity::new,ModBlocks.VANITY_MIRROR.get()).build(null));


    public static final RegistryObject<TileEntityType<PillowTileEntity>> PILLOW_TILE_ENTITY = TILE_ENTITY_TYPES.register("pillow", () ->
            TileEntityType.Builder.create(PillowTileEntity::new,ModBlocks.PILLOW.get()).build(null));
}
