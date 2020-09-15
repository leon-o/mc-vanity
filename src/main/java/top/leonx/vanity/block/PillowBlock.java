package top.leonx.vanity.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("deprecation")
public class PillowBlock extends HorizontalBlock {
    public PillowBlock() {
        super(Properties.create(Material.WOOL));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        BlockState stateBelow = context.getWorld().getBlockState(context.getPos().down());
        if(context.getFace()==Direction.UP && stateBelow.getBlock() instanceof BedBlock && stateBelow.get(BedBlock.PART)== BedPart.HEAD){
            BlockState state = super.getStateForPlacement(context);
            if (state != null) {
                state.with(HORIZONTAL_FACING,stateBelow.get(HORIZONTAL_FACING));
            }
            return state;
        }

        else return null;
    }


    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public boolean isTransparent(BlockState state) {
        return true;
    }
    VoxelShape shape=Block.makeCuboidShape(0,-16,0,16,0,16);
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return shape;
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if(!(worldIn.getBlockState(currentPos.down()).getBlock() instanceof BedBlock)){
            worldIn.destroyBlock(currentPos,true);
            return Blocks.AIR.getDefaultState();
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }
}
