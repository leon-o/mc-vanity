package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.util.TernaryFunc;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class PickItemTask<T extends OutsiderEntity> extends BehaviorTreeTask<T> {
    private final Predicate<ItemEntity> itemFinder;
    public        Vec3d                 findRange=new Vec3d(16,16,16);
    public PickItemTask(Predicate<ItemEntity> itemFinder) {
        this.itemFinder = itemFinder;
    }

    private List<ItemEntity> itemEntities;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        Vec3d posVec = entity.getPositionVec();
        itemEntities = entity.world.getEntitiesWithinAABB(EntityType.ITEM, new AxisAlignedBB(posVec.add(findRange.inverse()), posVec.add(findRange)), itemFinder);
        if (itemEntities.size() <= 0) {
            submitResult(Result.FAIL);
        }
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        Optional<ItemEntity> min = itemEntities.stream().min(Comparator.comparingDouble(entity::getDistanceSq));
        if(min.isPresent())
        {
            entity.getNavigator().tryMoveToEntityLiving(min.get(),entity.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
        }else{
            submitResult(Result.SUCCESS);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {

    }
}
