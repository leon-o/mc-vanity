package top.leonx.vanity.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.world.World;

public class PlayerSimPathNavigator extends GroundPathNavigator {
    public PlayerSimPathNavigator(MobEntity mobEntity, World worldIn) {
        super(mobEntity, worldIn);
    }


}
