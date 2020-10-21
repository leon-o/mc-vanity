package top.leonx.vanity.ai.goap;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ReachTargetGoapGoal extends GoapGoal{
    Vec3d pos;
    double error;
    public ReachTargetGoapGoal(Vec3d pos)
    {
        this(pos,0.5f);
    }
    public ReachTargetGoapGoal(Vec3d pos,double error) {
        super("reach_target",entity -> entity.getPositionVec().distanceTo(pos)<error);
        this.pos=pos;
        this.error=error;
    }

    public BlockPos getBlockPos()
    {
        return new BlockPos(pos.x,pos.y,pos.z);
    }
}
