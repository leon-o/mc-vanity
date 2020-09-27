package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.util.AIUtil;
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

    //private List<ItemEntity> itemEntities;
    private ItemEntity targetEntity;
    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        Vec3d posVec = entity.getPositionVec();
        List<ItemEntity> itemEntities = entity.world.getEntitiesWithinAABB(EntityType.ITEM, new AxisAlignedBB(posVec.add(findRange.inverse()), posVec.add(findRange)), itemFinder);
        if (itemEntities.size() <= 0) {
            submitResult(Result.FAIL);
            return;
        }
        Optional<ItemEntity> min = itemEntities.stream().min(Comparator.comparingDouble(entity::getDistanceSq));
        targetEntity=min.get();
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(targetEntity==null) {submitResult(Result.FAIL); return;}
        entity.getLookController().setLookPositionWithEntity(targetEntity,30,30);

        Path path = entity.getNavigator().getPathToEntity(targetEntity, 1);
        if (path != null) {
            entity.getNavigator().setPath(path, AIUtil.speedEase(entity.getDistanceSq(targetEntity)));
        }
        if(!targetEntity.isAlive())
            submitResult(Result.SUCCESS);
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        entity.getNavigator().clearPath();
        //noinspection deprecation
        entity.setAIMoveSpeed(0);
    }
}
