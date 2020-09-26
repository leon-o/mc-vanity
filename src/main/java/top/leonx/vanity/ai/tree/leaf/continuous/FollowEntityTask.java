package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class FollowEntityTask extends BehaviorTreeTask<OutsiderEntity> {
    public Function<OutsiderEntity,LivingEntity> targetGetter;

    public FollowEntityTask(Function<OutsiderEntity,LivingEntity> targetEntityGetter) {
        this.targetGetter = targetEntityGetter;
    }

    private LivingEntity  followedEntity;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        followedEntity = targetGetter.apply(entity);
        if(followedEntity==null)
            submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(entity.getDistanceSq(followedEntity)<9) {
            entity.getNavigator().clearPath();
            submitResult(Result.SUCCESS);
        }else{
            Path path = entity.getNavigator().getPathToEntity(followedEntity, 8);
            entity.getLookController().setLookPositionWithEntity(followedEntity,20,20);
            if(path!=null && entity.getDistanceSq(followedEntity)<900)
                entity.getNavigator().setPath(path,1f);
            else {
                this.tryToTeleportNearEntity(entity,followedEntity);
            }
        }
    }

    private void tryToTeleportNearEntity(OutsiderEntity entity,LivingEntity target) {
        BlockPos blockpos = target.getPosition();

        for(int i = 0; i < 10; ++i) {
            int j = this.getRandomNumber(-3, 3);
            int k = this.getRandomNumber(-1, 1);
            int l = this.getRandomNumber(-3, 3);
            boolean flag = this.tryToTeleportToLocation(entity,target,blockpos.getX() + j, blockpos.getY() + k, blockpos.getZ() + l);
            if (flag) {
                return;
            }
        }
    }
    private boolean tryToTeleportToLocation(OutsiderEntity entity,LivingEntity target,int xIn, int yIn, int zIn) {
        if (Math.abs((double)xIn - target.getPosX()) < 2.0D && Math.abs((double)zIn - target.getPosZ()) < 2.0D) {
            return false;
        } else if (!this.isTeleportFriendlyBlock(entity,target,new BlockPos(xIn, yIn, zIn))) {
            return false;
        } else {
            entity.setLocationAndAngles((float)xIn + 0.5F, yIn, (float)zIn + 0.5F, entity.rotationYaw, entity.rotationPitch);
            entity.getNavigator().clearPath();
            return true;
        }
    }
    private boolean isTeleportFriendlyBlock(OutsiderEntity entity,LivingEntity target,BlockPos posIn) {
        PathNodeType pathnodetype = WalkNodeProcessor.func_227480_b_(entity.world, posIn.getX(), posIn.getY(), posIn.getZ());
        if (pathnodetype != PathNodeType.WALKABLE) {
            return false;
        } else {
            BlockState blockstate = entity.world.getBlockState(posIn.down());
            //noinspection PointlessBooleanExpression
            if (true /*this.teleportToLeaves*/ && blockstate.getBlock() instanceof LeavesBlock) {
                return false;
            } else {
                BlockPos blockpos = posIn.subtract(entity.getPosition());
                return entity.world.hasNoCollisions(entity, entity.getBoundingBox().offset(blockpos));
            }
        }
    }
    private int getRandomNumber(int minIn, int maxIn) {
        return (int) (Math.random()*(maxIn - minIn + 1) + minIn);
    }
    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        entity.getNavigator().clearPath();
    }
}
