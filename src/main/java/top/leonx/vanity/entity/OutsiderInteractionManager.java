package top.leonx.vanity.entity;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.TreeMap;
import java.util.function.Consumer;

public class OutsiderInteractionManager {
    private static final Logger             field_225418_c    = LogManager.getLogger();
    public                ServerWorld        world;
    //private               GameType           gameType          = GameType.NOT_SET;
    private               boolean  isDestroyingBlock;
    private               int      destoryStartedTick;
    private               BlockPos destroyPos        = BlockPos.ZERO;
    private               int                ticks;
    private               boolean            receivedFinishDiggingPacket;
    private               BlockPos           delayedDestroyPos = BlockPos.ZERO;
    private int initialBlockDamage;
    private int durabilityRemainingOnBlock = -1;
    private OutsiderEntity entity;
    public OutsiderInteractionManager(OutsiderEntity entity) {
        this.entity=entity;
        if(entity.world.isRemote) return;
        this.world= (ServerWorld) entity.world;
    }
    Consumer<OutsiderEntity> onDestroyFinished;
    public void tick()
    {
        if(world==null) return;
        ticks++;
        processDestroyBlock();
    }
    public boolean startDestroyBlock(BlockPos pos, @Nullable Consumer<OutsiderEntity> onDestroyFinished)
    {
        if(world.getBlockState(pos).isAir(world, destroyPos)) return false;
        destroyPos=pos;
        destoryStartedTick=this.ticks;
        isDestroyingBlock=true;
        this.onDestroyFinished=onDestroyFinished;
        entity.setActiveHand(Hand.MAIN_HAND);

        return true;
    }
    public void stopDestroyBlock()
    {
        this.world.sendBlockBreakProgress(entity.getEntityId(), destroyPos, -1);
        destroyPos=BlockPos.ZERO;
        this.isDestroyingBlock = false;
        this.onDestroyFinished=null;
        if(entity.getActiveHand()==Hand.MAIN_HAND)
            entity.stopActiveHand();
    }
    private void processDestroyBlock()
    {
        if(isDestroyingBlock)
        {
            int duration = this.ticks - destoryStartedTick;
            BlockState blockState=world.getBlockState(destroyPos);
            float blockHardness = getEntityRelativeBlockHardness(blockState, world, destroyPos);

            if(!blockState.isAir(world, destroyPos) && blockHardness>=1.0)
            {
                finishDestroyBlock();
                return;
            }

            float hardnessMulDuration=  (blockHardness*(duration+1));
            if (hardnessMulDuration >= 1F) {
                finishDestroyBlock();
                return;
            }

            int destroyProcess= (int) (hardnessMulDuration*10);
            if(destroyProcess!=durabilityRemainingOnBlock) {
                world.sendBlockBreakProgress(entity.getEntityId(), destroyPos, destroyProcess);
                durabilityRemainingOnBlock=destroyProcess;
            }
        }
    }

    private void finishDestroyBlock() {
        this.tryHarvestBlock(destroyPos);

        if (onDestroyFinished != null) {
            onDestroyFinished.accept(entity);
        }

        stopDestroyBlock();
    }

    public float getEntityRelativeBlockHardness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        float f = state.getBlockHardness(worldIn, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i =  canHarvestBlock(state, worldIn, pos) ? 30 : 100;
            return entity.getDigSpeed(state, pos) / f / (float)i;
        }
    }
    public boolean canHarvestBlock(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos)
    {
        //state = state.getActualState(world, pos);
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = entity.getHeldItemMainhand();
        ToolType  tool  = state.getHarvestTool();
        if (stack.isEmpty() || tool == null)
        {
            return true;//entity.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, entity.getFakePlayer(), state);
        if (toolLevel < 0)
        {
            return true;
            //return player.canHarvestBlock(state);
        }

        return toolLevel >= state.getHarvestLevel();//ForgeEventFactory.doPlayerHarvestCheck(player, state, );
    }
    /**
     * Attempts to harvest a block
     */
    public boolean tryHarvestBlock(BlockPos pos) {
        BlockState blockstate = this.world.getBlockState(pos);
        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, GameType.SURVIVAL, entity.getFakePlayer(), pos);
        if (exp == -1) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(pos);
            Block      block      = blockstate.getBlock();
            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) /*&& !this.player.canUseCommandBlock()*/) {
                this.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
                return false;
            } else if (entity.getHeldItemMainhand().onBlockStartBreak(pos, entity.getFakePlayer())) {
                return false;
            } else {
                /*if (this.isCreative()) {
                    removeBlock(pos, false);
                    return true;
                } else {*/
                    ItemStack heldItem = entity.getHeldItemMainhand();
                    ItemStack heldItemCopy = heldItem.copy();

                    boolean flag1 = canHarvestBlock(world.getBlockState(pos),this.world, pos);
                    heldItem.onBlockDestroyed(this.world, blockstate, pos, entity.getFakePlayer());
                    if (heldItem.isEmpty() && !heldItemCopy.isEmpty())
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(entity.getFakePlayer(), heldItemCopy, Hand.MAIN_HAND);
                    boolean flag = removeBlock(pos, flag1);

                    if (flag && flag1) {
                        block.harvestBlock(this.world, entity.getFakePlayer(), pos, blockstate, tileentity, heldItemCopy);
                    }

                    if (flag && exp > 0)
                        blockstate.getBlock().dropXpOnBlockBreak(world, pos, exp);

                    return true;
                /*}*/
            }
        }
    }

    private boolean removeBlock(BlockPos pos, boolean canHarvest) {
        BlockState state = this.world.getBlockState(pos);
        boolean removed = state.removedByPlayer(this.world, pos, entity.getFakePlayer(), canHarvest, this.world.getFluidState(pos));
        if (removed)
            state.getBlock().onPlayerDestroy(this.world, pos, state);
        return removed;
    }
}
