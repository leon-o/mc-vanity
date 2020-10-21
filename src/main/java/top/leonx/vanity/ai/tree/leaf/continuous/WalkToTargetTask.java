package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

public class WalkToTargetTask <T extends MobEntity> extends BehaviorTreeTask<T>{
    private BlockPos pos;

    public WalkToTargetTask(BlockPos pos) {

        this.pos = pos;
    }


    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(pos==null)
            submitResult(BehaviorTreeTask.Result.FAIL);

        entity.getNavigator().tryMoveToXYZ(pos.getX(),pos.getY(),pos.getZ(),1f);

        if(entity.getNavigator().getPath()!=null && entity.getNavigator().getPath().isFinished())
            submitResult(BehaviorTreeTask.Result.SUCCESS);

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
