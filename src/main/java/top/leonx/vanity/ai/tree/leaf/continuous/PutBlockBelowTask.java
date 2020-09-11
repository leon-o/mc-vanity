package top.leonx.vanity.ai.tree.leaf.continuous;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.util.AIUtil;

import java.util.Comparator;


public class PutBlockBelowTask<T extends OutsiderEntity> extends BehaviorTreeTask<T> {

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        boolean held= entity.inventory.findAndHeld(Hand.MAIN_HAND,t-> t.getItem() instanceof BlockItem,
                                     Comparator.comparingDouble((Pair<Integer,ItemStack> t)->AIUtil.getItemValue(t.getSecond()))
                                             .thenComparingDouble((Pair<Integer,ItemStack> t)->((BlockItem)t.getSecond().getItem()).getBlock().getHarvestLevel(AIUtil.DUMMY_BLOCK_STATE)).reversed());
        if(!held)
            submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        BlockPos blockPos = entity.getPosition();
        Vec3d    blockCenter    = new Vec3d(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f);
        entity.getLookController().setLookPosition(blockCenter);
        entity.getNavigator().tryMoveToXYZ(blockCenter.x,blockCenter.y,blockCenter.z,entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        if(entity.onGround)
            entity.getJumpController().setJumping();

        BlockRayTraceResult blockRayTraceResult = entity.world.rayTraceBlocks(
                new RayTraceContext(entity.getPositionVec(), entity.getPositionVec().add(0f, -2f, 0f), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, entity));
        BlockPos blockPosBlow = blockRayTraceResult.getPos();
        if(entity.getPosY()-blockPosBlow.getY()>2 && entity.rotationPitch<85)
        {
            entity.placeHeldBlockOnLookAt();
        }

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
