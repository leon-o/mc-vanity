package top.leonx.vanity.ai.goap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class InteractGoapGoal extends GoapGoal{
    public final BlockPos pos;

    public InteractGoapGoal(BlockPos pos) {
        super("interact", entity -> pos.withinDistance(entity.getPositionVec(),
                                                                               entity.getBlockReachDistance()));
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }
    public Vec3d getPosVec()
    {
        return new Vec3d(pos.getX(),pos.getY(),pos.getZ());
    }
}
