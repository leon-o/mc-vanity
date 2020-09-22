package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

public class RandomWalkTask<T extends CreatureEntity> extends BehaviorTreeTask<T> {
    public Vec3d randomTarget;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        randomTarget=RandomPositionGenerator.findRandomTarget(entity, 10, 7);
        if(randomTarget==null)
            submitResult(Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if (entity.getNavigator().getPath()!=null && entity.getNavigator().getPath().isFinished()) {
            randomTarget=RandomPositionGenerator.findRandomTarget(entity, 10, 7);
        }
        if(randomTarget==null)
        {
            submitResult(Result.FAIL);
            return;
        }
        entity.getNavigator().tryMoveToXYZ(randomTarget.x,randomTarget.y,randomTarget.z,1);
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        entity.setAIMoveSpeed(0);
        entity.getNavigator().clearPath();
    }
}
