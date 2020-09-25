package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.Optional;
import java.util.function.Function;

public class LookAtNearestTask<T extends MobEntity> extends LookTurnToTask<T> {

    public LookAtNearestTask() {
        super(o->{
            Optional<PlayerEntity> nearest = o.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
            return nearest.map(playerEntity -> playerEntity.getEyePosition(0f)).orElse(null);
        });
    }
}
