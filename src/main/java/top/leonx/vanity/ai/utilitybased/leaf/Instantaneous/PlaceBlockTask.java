package top.leonx.vanity.ai.utilitybased.leaf.Instantaneous;

import com.google.common.collect.ImmutableList;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class PlaceBlockTask extends UtilityBasedTask<OutsiderEntity> {
    public ImmutableList<BlockItem>           blockItems;
    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        if(blockItems==null)
        {
            submitResult(Result.FAIL);
            return;
        }
        ActionResultType actionResult = entity.placeBlock(blockItems);
        submitResult(actionResult.isSuccess()?Result.SUCCESS:Result.FAIL);
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }
}
