package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.ArrayList;
import java.util.List;

public class SynchronousTask<T extends LivingEntity> extends CompositeTask<T> {

    public SynchronousTask(String name) {
        super(name);
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : getChildren()) {
            child.callForStart(world,entity,executionDuration);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : getChildren()) {
            child.callForUpdate(world,entity,executionDuration);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        for (BehaviorTreeTask<T> child : getChildren()) {
            child.callForEnd(world,entity,executionDuration);
        }
    }
}
