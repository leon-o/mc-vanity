package top.leonx.vanity.ai.tree.leaf.Instantaneous;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Supplier;

public class SwitchActivityTask<T extends OutsiderEntity> extends BehaviorTreeTask<T> {
    public Supplier<Activity> activitySupplier;

    public SwitchActivityTask(Supplier<Activity> activitySupplier) {
        this.activitySupplier = activitySupplier;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        entity.getBrain().stopAllTasks(world,entity);
        entity.getBrain().switchTo(activitySupplier.get());
        submitResult(Result.SUCCESS);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
