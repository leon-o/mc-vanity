package top.leonx.vanity.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.world.World;

public class PlayerSimPathNavigator extends GroundPathNavigator {
    public PlayerSimPathNavigator(MobEntity mobEntity, World worldIn) {
        super(mobEntity, worldIn);
    }
    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new WalkAndSwimNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }

}
