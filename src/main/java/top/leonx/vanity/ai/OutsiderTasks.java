package top.leonx.vanity.ai;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.monster.MonsterEntity;
import top.leonx.vanity.ai.tree.BehaviorTreeRootTask;
import top.leonx.vanity.ai.tree.composite.SelectorTask;
import top.leonx.vanity.ai.tree.composite.SequencesTask;
import top.leonx.vanity.ai.tree.composite.SynchronousTask;
import top.leonx.vanity.ai.tree.composite.UtilitySelectTask;
import top.leonx.vanity.ai.tree.leaf.continuous.*;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.util.AIUtil;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutsiderTasks {
    public static SequencesTask<OutsiderEntity> findFoodAndEat() {
        SequencesTask<OutsiderEntity> findFoodAndEatTask = new SequencesTask<>();

        PickItemTask<OutsiderEntity> pickFoodTask = new PickItemTask<>(t -> t.getItem().isFood());

        SequencesTask<OutsiderEntity> attackEntityForFood = new SequencesTask<>();
        ///attackEntityForFood.children.add(new FindAttackTargetTask<>();
        attackEntityForFood.children.add(new AttackTargetTask(AIUtil::getClosestFoodProvider));
        attackEntityForFood.children.add(pickFoodTask);

        findFoodAndEatTask.children.add(pickFoodTask);  //先尝试在地上寻找
        findFoodAndEatTask.children.add(attackEntityForFood);   //如果没找到，击杀实体以获取食物s
        findFoodAndEatTask.children.add(new EatFoodTask());     //从背包里拿出来吃掉
        return findFoodAndEatTask;
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> idle(float p_220641_1_) {
        return ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(
                ImmutableList.of(Pair.of(InteractWithEntityTask.func_220445_a(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 2),
                                 Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 1),
                                 Pair.of(new FindWalkTargetTask(p_220641_1_), 1), Pair.of(new WalkToTargetTask(200), 1), Pair.of(new JumpOnBedTask(p_220641_1_), 1)))));
    }

    public static SelectorTask<OutsiderEntity> increaseSatiety() {
        SelectorTask<OutsiderEntity> feedSelfTask = new SelectorTask<>();
        feedSelfTask.children.add(new EatFoodTask()); //从背包里找食物吃
        feedSelfTask.children.add(findFoodAndEat()); //背包里没有就要去找

        return feedSelfTask;
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> panic(float p_220636_1_) {
        float f = p_220636_1_ * 1.5F;
        return ImmutableList.of(Pair.of(1, new FleeTask(MemoryModuleType.NEAREST_HOSTILE, f)), Pair.of(1, new FleeTask(MemoryModuleType.HURT_BY_ENTITY, f)),
                                Pair.of(3, new FindWalkTargetTask(f, 2, 2)), lookAtPlayerOrVillager());
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> protectPlayer() {
        UtilitySelectTask<OutsiderEntity> utilitySelectTask = new UtilitySelectTask<>();

        //闲置
        utilitySelectTask.addChild((w,e,t)->0.06,new IdleTask<>());
        //跟随玩家
        utilitySelectTask.addChild((w, e, t) ->e.getFollowedPlayer()==null?0:AIUtil.sigmod(e.getDistance(e.getFollowedPlayer()), 0.4, 5), new FollowEntityTask(OutsiderEntity::getFollowedPlayer));
        //战斗
        utilitySelectTask.addChild((w, e, t) -> {
            Optional<List<LivingEntity>> visibleMobsOptional = e.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
            if (visibleMobsOptional.isPresent()) { //存在威胁者，效用值为1否则为0
                List<LivingEntity> visibleMobs = visibleMobsOptional.get();
                boolean hasMenace  = visibleMobs.stream().filter(entity->entity instanceof MobEntity).map(entity->(MobEntity)entity).anyMatch(
                        (MobEntity mob) -> mob.getAttackTarget()!=null && (Objects.equals(mob.getAttackTarget(), e.getFollowedPlayer()) || Objects.equals(mob.getAttackTarget(), e)));

                return hasMenace ? 0.5d : 0d;
            } else return 0d;
        }, battle());

        //自我保护
        utilitySelectTask.addChild((w, e, t) -> AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -8, -5), selfProtection());

        return ImmutableList.of(new Pair<>(1, new BehaviorTreeRootTask<>(utilitySelectTask)));
    }

    private static UtilitySelectTask<OutsiderEntity> battle() {
        UtilitySelectTask<OutsiderEntity> battleTask = new UtilitySelectTask<>();
        battleTask.addChild((w, e, t) -> {
            if (e.getAttackTarget() == null) return 0d;
            return (double) (e.getHealth() / e.getMaxHealth()); //生命越多越勇
        }, new AttackTargetTask(AIUtil::getMostDangerousEntityNear));

        battleTask.addChild((w, e, t) -> {
            Optional<LivingEntity> hurtBy = e.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
            if (hurtBy.isPresent()) return 1d - e.getHealth() / e.getMaxHealth();
            return 0d;
        }, new DefendWithShieldTask());

        return battleTask;
    }

    private static Pair<Integer, Task<LivingEntity>> lookAtMany() {
        return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.CAT, 8.0F), 8), Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2),
                                                                   Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityClassification.CREATURE, 8.0F), 1),
                                                                   Pair.of(new LookAtEntityTask(EntityClassification.WATER_CREATURE, 8.0F), 1),
                                                                   Pair.of(new LookAtEntityTask(EntityClassification.MONSTER, 8.0F), 1), Pair.of(new DummyTask(30, 60), 2))));
    }

    private static Pair<Integer, Task<LivingEntity>> lookAtPlayerOrVillager() {
        return Pair.of(5, new FirstShuffledTask<>(
                ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new DummyTask(30, 60), 8))));
    }

    private static SynchronousTask<OutsiderEntity> selfProtection() {
        SynchronousTask<OutsiderEntity> synchronousTask = new SynchronousTask<>();
        synchronousTask.children.add(new EscapeFromTask<>());
        UtilitySelectTask<OutsiderEntity> treatSelfTask      = new UtilitySelectTask<>();
        SelectorTask<OutsiderEntity>      usePotionOrEatFood = new SelectorTask<>(); //如果没有药水，就只能吃东西
        usePotionOrEatFood.children.add(new UsePotionTask());
        usePotionOrEatFood.children.add(increaseSatiety());
        treatSelfTask.addChild((w, e, t) -> AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -15, -5), usePotionOrEatFood);
        treatSelfTask.addChild((w, e, t) -> 0.8 * AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -8, -5), increaseSatiety());
        synchronousTask.children.add(treatSelfTask);

        return synchronousTask;
    }
}
