package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

public class UsePotionTask extends BehaviorTreeTask<OutsiderEntity> {
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        submitResult(Result.FAIL);
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }
}
