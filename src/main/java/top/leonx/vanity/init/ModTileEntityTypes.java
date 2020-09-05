package top.leonx.vanity.init;

import net.minecraft.tileentity.TileEntityType;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;

public class ModTileEntityTypes {
    public static final TileEntityType<VanityMirrorTileEntity> VANITY_MIRROR_TILE_ENTITY_TILE_ENTITY_TYPE=
            TileEntityType.Builder.create(VanityMirrorTileEntity::new,ModBlocks.VANITY_MIRROR).build(null);
}
