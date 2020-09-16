package top.leonx.vanity.init;

import net.minecraft.tileentity.TileEntityType;
import top.leonx.vanity.tileentity.PillowTileEntity;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;

public class ModTileEntityTypes {
    public static final TileEntityType<VanityMirrorTileEntity> VANITY_MIRROR_TILE_ENTITY_TILE_ENTITY_TYPE=
            TileEntityType.Builder.create(VanityMirrorTileEntity::new,ModBlocks.VANITY_MIRROR).build(null);
    public static final TileEntityType<PillowTileEntity> PILLOW_TILE_ENTITY=TileEntityType.Builder.create(PillowTileEntity::new,ModBlocks.PILLOW).build(null);
}
