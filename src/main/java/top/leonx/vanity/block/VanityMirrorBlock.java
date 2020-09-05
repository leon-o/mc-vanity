package top.leonx.vanity.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.init.ModTileEntityTypes;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;

import javax.annotation.Nullable;

public class VanityMirrorBlock extends Block {
    public VanityMirrorBlock() {
        super(Properties.create(Material.GLASS));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.VANITY_MIRROR_TILE_ENTITY_TILE_ENTITY_TYPE.create();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(worldIn.isRemote)return ActionResultType.PASS;
        TileEntity entity = worldIn.getTileEntity(pos);
        if(entity instanceof VanityMirrorTileEntity)
        {
            NetworkHooks.openGui((ServerPlayerEntity) player, (VanityMirrorTileEntity)entity,pos);

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }
}
