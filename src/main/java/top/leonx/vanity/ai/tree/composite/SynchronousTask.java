package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.ArrayList;
import java.util.List;

public class SynchronousTask<T extends LivingEntity> extends BehaviorTreeTask<T> {
    public final List<BehaviorTreeTask<T>> children =new ArrayList<>();
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : children) {
            child.callForStart(world,entity,executionDuration);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : children) {
            child.callForUpdate(world,entity,executionDuration);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : children) {
            child.callForEnd(world,entity,executionDuration);
        }
    }
}
