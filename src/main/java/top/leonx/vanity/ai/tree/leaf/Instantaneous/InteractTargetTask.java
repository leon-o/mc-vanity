package top.leonx.vanity.ai.tree.leaf.Instantaneous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

public class InteractTargetTask<T extends LivingEntity> extends BehaviorTreeTask<T> {

    private final BlockPos pos;

    public InteractTargetTask(BlockPos pos)
    {

        this.pos = pos;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
