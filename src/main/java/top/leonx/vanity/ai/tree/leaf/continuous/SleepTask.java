package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.util.AIUtil;

import java.util.Optional;

public class SleepTask<T extends MobEntity> extends BehaviorTreeTask<T> {
    BlockPos bedPos;

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        Optional<BlockPos> bedPositionOpt = entity.getBrain().getMemory(MemoryModuleType.NEAREST_BED);

        if (bedPositionOpt.isPresent()) bedPos = bedPositionOpt.get();
        else submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(bedPos==null)
        {
            submitResult(Result.FAIL);
            return;
        }
        if (!entity.isSleeping()) {
            Vec3d bedPosVec = new Vec3d(bedPos);
            entity.getNavigator().tryMoveToXYZ(bedPosVec.x, bedPosVec.y, bedPosVec.z, AIUtil.sigmod(entity.getDistanceSq(bedPosVec),2,+4));
            if (entity.getDistanceSq(bedPosVec) <= 3.2) {
                entity.getNavigator().clearPath();
                entity.startSleeping(bedPos);
            }
        }else
        {
            BlockState state =world.getBlockState(entity.getPosition());
            if(!state.getBlock().isBed(state,world,entity.getPosition(),null)){
                entity.wakeUp();
                submitResult(Result.FAIL);
            }

        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        entity.wakeUp();
    }
}
