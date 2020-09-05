package top.leonx.vanity.entity.ai.brain.utilitybased;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.entity.ai.brain.utilitybased.leaf.AttackTargetTask;
import top.leonx.vanity.entity.ai.brain.utilitybased.leaf.DefendWithShieldTask;
import top.leonx.vanity.entity.ai.brain.utilitybased.leaf.FindAttackTargetTask;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.util.AIUtil;

import java.util.function.Predicate;

public class OutsiderTasks {
    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> protectPlayer()
    {
        UtilitySelectTask<OutsiderEntity> utilitySelectTask =new UtilitySelectTask<>(ImmutableMap.of());
        utilitySelectTask.addChild( new AttackTargetTask());
        utilitySelectTask.addChild( new DefendWithShieldTask());
        //utilityBasedTask.children.put(t->0.8, new InertiaTask<>(new DefendWithShieldTask(),120));
        Predicate<Entity> attackTargetFilter=entity -> entity instanceof  LivingEntity &&AIUtil.getLivingEntityDrops((LivingEntity)entity).stream().anyMatch(t->t.getItem().isFood());
        return ImmutableList.of(new Pair<>(1, utilitySelectTask), new Pair<>(2, new FindAttackTargetTask(attackTargetFilter)));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> idle(float p_220641_1_) {
        return ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(
                ImmutableList.of(Pair.of(InteractWithEntityTask.func_220445_a(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 2),
                                 Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 1),
                                 Pair.of(new FindWalkTargetTask(p_220641_1_), 1), Pair.of(new WalkToTargetTask(200), 1), Pair.of(new JumpOnBedTask(p_220641_1_), 1)))));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> panic(float p_220636_1_) {
        float f = p_220636_1_ * 1.5F;
        return ImmutableList.of(Pair.of(1, new FleeTask(MemoryModuleType.NEAREST_HOSTILE, f)), Pair.of(1, new FleeTask(MemoryModuleType.HURT_BY_ENTITY, f)), Pair.of(3, new FindWalkTargetTask(f, 2, 2)), lookAtPlayerOrVillager());
    }
//
//    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> preRaid(VillagerProfession profession, float p_220642_1_) {
//        return ImmutableList.of(Pair.of(0, new RingBellTask()), Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new StayNearPointTask(MemoryModuleType.MEETING_POINT, p_220642_1_ * 1.5F, 2, 150, 200), 6), Pair.of(new FindWalkTargetTask(p_220642_1_ * 1.5F), 2)))), lookAtPlayerOrVillager(), Pair.of(99, new ForgetRaidTask()));
//    }
//
//    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> raid(VillagerProfession profession, float p_220640_1_) {
//        return ImmutableList.of(Pair.of(0, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new GoOutsideAfterRaidTask(p_220640_1_), 5), Pair.of(new FindWalkTargetAfterRaidVictoryTask(p_220640_1_ * 1.1F), 2)))), Pair.of(0, new CelebrateRaidVictoryTask(600, 600)), Pair.of(2, new FindHidingPlaceDuringRaidTask(24, p_220640_1_ * 1.4F)), lookAtPlayerOrVillager(), Pair.of(99, new ForgetRaidTask()));
//    }
//
//    public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> hide(VillagerProfession profession, float p_220644_1_) {
//        int i = 2;
//        return ImmutableList.of(Pair.of(0, new ExpireHidingTask(15, 2)), Pair.of(1, new FindHidingPlaceTask(32, p_220644_1_ * 1.25F, 2)), lookAtPlayerOrVillager());
//    }

    private static Pair<Integer, Task<LivingEntity>> lookAtMany() {
        return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.CAT, 8.0F), 8), Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new LookAtEntityTask(
                EntityClassification.CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.WATER_CREATURE, 8.0F), 1), Pair.of(new LookAtEntityTask(EntityClassification.MONSTER, 8.0F), 1), Pair.of(new DummyTask(30, 60), 2))));
    }

    private static Pair<Integer, Task<LivingEntity>> lookAtPlayerOrVillager() {
        return Pair.of(5, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new LookAtEntityTask(EntityType.VILLAGER, 8.0F), 2), Pair.of(new LookAtEntityTask(EntityType.PLAYER, 8.0F), 2), Pair.of(new DummyTask(30, 60), 8))));
    }
}
