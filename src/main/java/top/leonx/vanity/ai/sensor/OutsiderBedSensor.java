package top.leonx.vanity.ai.sensor;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class OutsiderBedSensor extends Sensor<OutsiderEntity> {
    private final Long2LongMap posToPosMap = new Long2LongOpenHashMap();
    private       int          checkTimes;
    private       long         fadeTime;

    public OutsiderBedSensor() {
        super(20); //interval
    }

    @Override
    public Set<MemoryModuleType<?>> getUsedMemories() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_BED);
    }

    @Override
    protected void update(ServerWorld worldIn, OutsiderEntity entityIn) {
        Optional<BlockPos> bedPosition = entityIn.getBedPosition();
        if (bedPosition.isPresent() && worldIn.isAreaLoaded(bedPosition.get(), 1)) {
            entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_BED, bedPosition.get());
            return;
        }

        this.checkTimes = 0;
        this.fadeTime = worldIn.getGameTime() + (long) worldIn.getRandom().nextInt(20);

        PointOfInterestManager pointOfInterestManager = worldIn.getPointOfInterestManager();
        Predicate<BlockPos> predicate = (pos) -> {
            long i = pos.toLong();
            if (this.posToPosMap.containsKey(i)) {
                return false;
            } else if (++this.checkTimes >= 5) {
                return false;
            } else {
                this.posToPosMap.put(i, this.fadeTime + 40L);
                return true;
            }
        };
        Stream<BlockPos> stream = pointOfInterestManager.findAll(PointOfInterestType.HOME.getPredicate(), predicate, new BlockPos(entityIn), 48, PointOfInterestManager.Status.HAS_SPACE);
        Optional<BlockPos> nearestBed = stream.filter(t -> {
            Path path = entityIn.getNavigator().getPathToPos(t, 1);
            return path != null && path.reachesTarget();
        }).min(Comparator.comparingDouble(t -> entityIn.getDistanceSq(new Vec3d(t))));
        entityIn.getBrain().setMemory(MemoryModuleType.NEAREST_BED, nearestBed);

        if (!nearestBed.isPresent()) {
            if (this.checkTimes < 5) {
                this.posToPosMap.long2LongEntrySet().removeIf((pos) -> pos.getLongValue() < this.fadeTime);
            }
        }
    }
}
