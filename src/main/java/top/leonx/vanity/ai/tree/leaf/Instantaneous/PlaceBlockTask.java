package top.leonx.vanity.ai.tree.leaf.Instantaneous;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

public class PlaceBlockTask extends BehaviorTreeTask<OutsiderEntity> {
    public ImmutableList<BlockItem>           blockItems;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(blockItems==null)
        {
            submitResult(Result.FAIL);
            return;
        }
        ActionResultType actionResult = entity.interactionManager.placeBlock(blockItems);
        submitResult(actionResult.isSuccess()?Result.SUCCESS:Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }
}
