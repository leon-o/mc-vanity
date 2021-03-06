package top.leonx.vanity.entity;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class OutsiderInteractionManager {
    public                ServerWorld        world;
    //private               GameType           gameType          = GameType.NOT_SET;
    private               boolean  isDestroyingBlock;
    private               int      destroyStartedTick;
    private               BlockPos destroyPos        = BlockPos.ZERO;
    private               int                ticks;
    private       int                      durabilityRemainingOnBlock = -1;
    private final OutsiderEntity           outsider;
    private       Consumer<OutsiderEntity> itemUseFinishedConsumer;

    public OutsiderInteractionManager(OutsiderEntity outsider) {
        this.outsider = outsider;
        if(outsider.world.isRemote) return;
        this.world= (ServerWorld) outsider.world;
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
        destroyStartedTick =this.ticks;
        isDestroyingBlock=true;
        this.onDestroyFinished=onDestroyFinished;
        //noinspection deprecation
        outsider.setActiveHand(Hand.MAIN_HAND);

        return true;
    }
    public void stopDestroyBlock()
    {
        this.world.sendBlockBreakProgress(outsider.getEntityId(), destroyPos, -1);
        destroyPos=BlockPos.ZERO;
        this.isDestroyingBlock = false;
        this.onDestroyFinished=null;
        if(outsider.getActiveHand()==Hand.MAIN_HAND)
            //noinspection deprecation
            outsider.stopActiveHand();
    }
    private void processDestroyBlock()
    {
        if(isDestroyingBlock)
        {
            int duration = this.ticks - destroyStartedTick;
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
            outsider.swingArm(Hand.MAIN_HAND);
            int destroyProcess= (int) (hardnessMulDuration*10);
            if(destroyProcess!=durabilityRemainingOnBlock) {
                world.sendBlockBreakProgress(outsider.getEntityId(), destroyPos, destroyProcess);
                durabilityRemainingOnBlock=destroyProcess;
            }
        }
    }

    private void finishDestroyBlock() {
        this.tryHarvestBlock(destroyPos);

        if (onDestroyFinished != null) {
            onDestroyFinished.accept(outsider);
        }

        stopDestroyBlock();
    }

    public float getEntityRelativeBlockHardness(BlockState state, IBlockReader worldIn, BlockPos pos) {
        float f = state.getBlockHardness(worldIn, pos);
        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i =  canHarvestBlock(state, worldIn, pos) ? 30 : 100;
            return outsider.getDigSpeed(state, pos) / f / (float)i;
        }
    }
    public boolean canHarvestBlock(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos)
    {
        //state = state.getActualState(world, pos);
        if (state.getMaterial().isToolNotRequired())
        {
            return true;
        }

        ItemStack stack = outsider.getHeldItemMainhand();
        ToolType  tool  = state.getHarvestTool();
        if (stack.isEmpty() || tool == null)
        {
            return true;//entity.canHarvestBlock(state);
        }

        int toolLevel = stack.getItem().getHarvestLevel(stack, tool, outsider.getFakePlayer(), state);
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
        outsider.getFakePlayer().setHeldItem(Hand.MAIN_HAND, outsider.getHeldItemMainhand());
        BlockState blockstate = this.world.getBlockState(pos);
        int exp = net.minecraftforge.common.ForgeHooks.onBlockBreakEvent(world, GameType.SURVIVAL, outsider.getFakePlayer(), pos);
        if (exp == -1) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(pos);
            Block      block      = blockstate.getBlock();
            if ((block instanceof CommandBlockBlock || block instanceof StructureBlock || block instanceof JigsawBlock) /*&& !this.player.canUseCommandBlock()*/) {
                this.world.notifyBlockUpdate(pos, blockstate, blockstate, 3);
                return false;
            } else if (outsider.getHeldItemMainhand().onBlockStartBreak(pos, outsider.getFakePlayer())) {
                return false;
            } else {
                /*if (this.isCreative()) {
                    removeBlock(pos, false);
                    return true;
                } else {*/
                    ItemStack heldItem = outsider.getHeldItemMainhand();
                    ItemStack heldItemCopy = heldItem.copy();

                    boolean flag1 = canHarvestBlock(world.getBlockState(pos),this.world, pos);
                    heldItem.onBlockDestroyed(this.world, blockstate, pos, outsider.getFakePlayer());
                    if (heldItem.isEmpty() && !heldItemCopy.isEmpty())
                        net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(outsider.getFakePlayer(), heldItemCopy, Hand.MAIN_HAND);
                    boolean flag = removeBlock(pos, flag1);

                    if (flag && flag1) {
                        block.harvestBlock(this.world, outsider.getFakePlayer(), pos, blockstate, tileentity, heldItemCopy);
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
        boolean removed = state.removedByPlayer(this.world, pos, outsider.getFakePlayer(), canHarvest, this.world.getFluidState(pos));
        if (removed)
            state.getBlock().onPlayerDestroy(this.world, pos, state);
        return removed;
    }

    @SuppressWarnings("unused")
    public ActionResultType placeBlock(ImmutableList<BlockItem> blockItems) {
        if (world.isRemote) return ActionResultType.PASS;
        //ServerPlayerEntity fakePlayer = getFakePlayer();

        //noinspection SuspiciousMethodCalls
        outsider.inventory.findAndHeld(Hand.MAIN_HAND, t -> t.getItem() instanceof BlockItem && blockItems.contains(t.getItem()), ItemStack::getCount);

        return placeHeldBlockOnLookAt();
    }

    public ActionResultType placeHeldBlockOnLookAt() {
        if (world.isRemote) return ActionResultType.PASS;
        ItemStack stack = outsider.getHeldItemMainhand();
        if (!(stack.getItem() instanceof BlockItem)) return ActionResultType.FAIL;
        //ServerPlayerEntity fakePlayer = entity.getFakePlayer();
        //fakePlayer.setHeldItem(Hand.MAIN_HAND, stack);

        Vec3d eyePos  = outsider.getEyePosition(1F);
        Vec3d lookVec = outsider.getLookVec();
        Vec3d endPos  = eyePos.add(lookVec.scale(outsider.getBlockReachDistance()));

        RayTraceContext     rayTraceContext = new RayTraceContext(eyePos, endPos, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, outsider);
        BlockRayTraceResult rayTraceResult  = world.rayTraceBlocks(rayTraceContext);
        return processPlaceBlock(world,stack,Hand.MAIN_HAND,rayTraceResult);//fakePlayer.interactionManager.func_219441_a(fakePlayer, world, stack, Hand.MAIN_HAND, rayTraceResult);
    }


    public ActionResultType processPlaceBlock(World worldIn, ItemStack stackIn, Hand handIn, BlockRayTraceResult blockRaytraceResultIn) {
        BlockPos blockpos = blockRaytraceResultIn.getPos();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        outsider.getFakePlayer().setHeldItem(handIn, stackIn);
        net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event = net.minecraftforge.common.ForgeHooks.onRightClickBlock(outsider.getFakePlayer(), handIn, blockpos,
                                                                                                                                                  blockRaytraceResultIn.getFace());
        if (event.isCanceled()) return event.getCancellationResult();
            ItemUseContext itemusecontext = new ItemUseContext(outsider.getFakePlayer(), handIn, blockRaytraceResultIn);
            if (event.getUseItem() != net.minecraftforge.eventbus.api.Event.Result.DENY) {
                ActionResultType result = stackIn.onItemUseFirst(itemusecontext);
                if (result != ActionResultType.PASS) return result;
            }
            boolean isHeldItem = !outsider.getHeldItemMainhand().isEmpty() || !outsider.getHeldItemOffhand().isEmpty();
            boolean heldShift =
                    (outsider.isSecondaryUseActive() && isHeldItem) && !(outsider.getHeldItemMainhand().doesSneakBypassUse(worldIn, blockpos, outsider.getFakePlayer()) && outsider.getHeldItemOffhand().doesSneakBypassUse(worldIn, blockpos, outsider.getFakePlayer()));
            if (event.getUseBlock() != net.minecraftforge.eventbus.api.Event.Result.DENY && !heldShift) {
                ActionResultType actionresulttype = blockstate.onBlockActivated(worldIn, outsider.getFakePlayer(), handIn, blockRaytraceResultIn);
                if (actionresulttype.isSuccessOrConsume()) {
                    return actionresulttype;
                }
            }

            if (!stackIn.isEmpty() && !outsider.getCooldownTracker().hasCooldown(stackIn.getItem())) {
                if (event.getUseItem() == net.minecraftforge.eventbus.api.Event.Result.DENY)
                    return ActionResultType.PASS;
                return stackIn.onItemUse(itemusecontext);
            } else {
                return ActionResultType.PASS;
            }

    }

    /**
     * Use the item in main hand.
     * @param onUseFinished the consumer will be called in {@link OutsiderEntity#onItemUseFinish()} when item use finished.
     */
    public void useItemInMainHand(@Nullable Consumer<OutsiderEntity> onUseFinished) {
        //noinspection deprecation
        outsider.setActiveHand(Hand.MAIN_HAND);
        itemUseFinishedConsumer = onUseFinished;
    }


    public void stopUseItem()
    {
        //noinspection deprecation
        outsider.stopActiveHand();
        itemUseFinishedConsumer=null;
    }
    /**
     * Called when item use finished.
     * If the onItemUseFinished isn't null, it will be called.
     * Called from {@link OutsiderInteractionManager#useItemInMainHand}
     */
    public void itemUseFinished()
    {
        if (itemUseFinishedConsumer == null) return;
        itemUseFinishedConsumer.accept(outsider);
        itemUseFinishedConsumer=null;
    }

    /**
     * Attack something this entity look at.
     * Create EntityRayTraceResult and then call {@link OutsiderEntity#attackEntityAsMob(Entity)}
     * @return is successful
     */
    public boolean attackLootAt() {
        double maxDist       = outsider.getBlockReachDistance();
        Vec3d  startVec      = outsider.getEyePosition(1f);
        Vec3d  lookDirection = outsider.getLook(1F);
        Vec3d  endVec        = startVec.add(lookDirection.scale(maxDist));
        EntityRayTraceResult traceResult = ProjectileHelper.rayTraceEntities(world, outsider, startVec, endVec, outsider.getBoundingBox().expand(lookDirection.scale(maxDist)).expand(1, 1, 1),
                                                                             (e) -> e instanceof LivingEntity && outsider.canAttack((LivingEntity) e), maxDist * maxDist);
        if (traceResult != null) {
            LivingEntity target = (LivingEntity) traceResult.getEntity();
            return outsider.attackEntityAsMob(target);
        }

        return false;
    }
}
