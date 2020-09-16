package top.leonx.vanity.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import top.leonx.vanity.init.ModTileEntityTypes;
import top.leonx.vanity.tileentity.PillowTileEntity;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class PillowBlock extends HorizontalBlock {
    VoxelShape shape = Block.makeCuboidShape(0, -16, 0, 16, 0, 16);

    public PillowBlock() {
        super(Properties.create(Material.WOOL));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState stateBelow = context.getWorld().getBlockState(context.getPos().down());
        if (context.getFace() == Direction.UP && stateBelow.getBlock() instanceof BedBlock && stateBelow.get(BedBlock.PART) == BedPart.HEAD) {
            BlockState state = super.getStateForPlacement(context);
            if (state != null) {
                state=state.with(HORIZONTAL_FACING, stateBelow.get(HORIZONTAL_FACING));
            }
            return state;
        } else return null;
    }

    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof PillowTileEntity) {
            ((PillowTileEntity) tileentity).readFromItemStack(stack);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (!(worldIn.getBlockState(currentPos.down()).getBlock() instanceof BedBlock)) {
            worldIn.destroyBlock(currentPos, true);
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntityTypes.PILLOW_TILE_ENTITY.create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING);
    }
}
