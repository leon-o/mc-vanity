package top.leonx.vanity.ai.utilitybased.leaf.continuous;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.utilitybased.UtilityBasedTask;
import top.leonx.vanity.util.TernaryFunc;

import java.util.function.Function;

public class LookTurnToTask<T extends MobEntity> extends UtilityBasedTask<T> {
    public Function<T,Vec3d> targetPosGetter;
    private final static int timeout=100;
    public LookTurnToTask(Function<T,Vec3d> targetPos) {
        this.targetPosGetter = targetPos;
    }

    public LookTurnToTask(TernaryFunc<ServerWorld, T, Long, Double> calculator, Function<T,Vec3d> targetPos) {
        super(calculator);
        this.targetPosGetter = targetPos;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {

    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        Vec3d targetPos = targetPosGetter.apply(entity);
        entity.getLookController().setLookPosition(targetPos);
        Vec3d targetLookVec = targetPos.add(entity.getEyePosition(1F).inverse());
        if(targetLookVec.dotProduct(entity.getLookVec())<1E-2)
            submitResult(Result.SUCCESS);

        if(executionDuration>100)
            submitResult(Result.FAIL);
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
