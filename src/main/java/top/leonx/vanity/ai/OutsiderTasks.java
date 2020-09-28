package top.leonx.vanity.ai;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.goap.EatFoodGoapGoal;
import top.leonx.vanity.ai.tree.BehaviorTreeRootTask;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.ai.tree.composite.*;
import top.leonx.vanity.ai.tree.leaf.Instantaneous.LookTurnToTask;
import top.leonx.vanity.ai.tree.leaf.Instantaneous.SwitchActivityTask;
import top.leonx.vanity.ai.tree.leaf.continuous.*;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.util.AIUtil;
import top.leonx.vanity.entity.GeneralFoodStats;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class OutsiderTasks {

    public static BehaviorTreeTask<OutsiderEntity> findFood()
    {
        SelectorTask<OutsiderEntity> findFoodTask=new SelectorTask<>("Find Food");
        PickItemTask<OutsiderEntity> pickFoodTask = new PickItemTask<>(t -> t.getItem().isFood());

        SequencesTask<OutsiderEntity> attackEntityForFood = new SequencesTask<>("Attack For Food");
        ///attackEntityForFood.children.add(new FindAttackTargetTask<>();
        attackEntityForFood.addChild(new AttackTargetTask(AIUtil::getNearestFoodProvider));
        attackEntityForFood.addChild(pickFoodTask);

        findFoodTask.addChild(pickFoodTask); //先尝试在地上寻找
        findFoodTask.addChild(attackEntityForFood);   //如果没找到，击杀实体以获取食

        return findFoodTask;
    }
    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> idle(float p_220641_1_) {
        return ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(
                ImmutableList.of(Pair.of(InteractWithEntityTask.func_220445_a(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.get(), 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 2),
                                 Pair.of(InteractWithEntityTask.func_220445_a(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, p_220641_1_, 2), 1),
                                 Pair.of(new FindWalkTargetTask(p_220641_1_), 1), Pair.of(new WalkToTargetTask(200), 1), Pair.of(new JumpOnBedTask(p_220641_1_), 1)))));
    }

    public static SelectorTask<OutsiderEntity> increaseSatiety() {
        SelectorTask<OutsiderEntity> feedSelfTask = new SelectorTask<>("Feed Self");
        feedSelfTask.addChild(new EatFoodTask()); //从背包里找食物吃

        SequencesTask<OutsiderEntity> findFoodThenEatTask = new SequencesTask<>("Find Food Then Eat");
        findFoodThenEatTask.addChild(findFood());
        findFoodThenEatTask.addChild(new EatFoodTask());

        feedSelfTask.addChild(findFoodThenEatTask); //背包里没有就要去找

        return feedSelfTask;
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> panic(float p_220636_1_) {
        float f = p_220636_1_ * 1.5F;
        return ImmutableList.of(Pair.of(1, new FleeTask(MemoryModuleType.NEAREST_HOSTILE, f)), Pair.of(1, new FleeTask(MemoryModuleType.HURT_BY_ENTITY, f)),
                                Pair.of(3, new FindWalkTargetTask(f, 2, 2)), lookAtPlayerOrVillager());
    }


    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> protectPlayer() {
        UtilitySelectTask<OutsiderEntity> utilitySelectTask = new UtilitySelectTask<>("Protect Player");

        //闲置
        utilitySelectTask.addChild((w,e,t)->0.06, new LookAtNearestTask<>());
        //跟随玩家
        utilitySelectTask.addChild((w, e, t) ->e.getFollowedPlayer()==null?0:AIUtil.sigmod(e.getDistance(e.getFollowedPlayer()), 0.4, 5), new FollowEntityTask(OutsiderEntity::getFollowedPlayer));
        //战斗
        utilitySelectTask.addChild(( w, e, t) -> {
            Optional<List<LivingEntity>> visibleMobsOptional = e.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
            if (visibleMobsOptional.isPresent()) { //存在威胁者，效用值为0.5否则为0
                List<LivingEntity> visibleMobs = visibleMobsOptional.get();
                boolean hasMenace  = visibleMobs.stream().filter(entity->entity instanceof MobEntity).map(entity->(MobEntity)entity).anyMatch(
                        (MobEntity mob) -> mob.getAttackTarget()!=null && (Objects.equals(mob.getAttackTarget(), e.getFollowedPlayer()) || Objects.equals(mob.getAttackTarget(), e)));

                return hasMenace ? 0.5d : 0d;
            } else return 0d;
        }, battle());

        //当不在战斗状态，并且饿了，就吃点东西
        utilitySelectTask.addChild((ServerWorld w, OutsiderEntity e, Long t)->{
            if(e.getFoodStats().needFood())
                return 0.4d;
            else
                return 0d;
        },new GoapTask("eat",new EatFoodGoapGoal()));

        //自我保护
        utilitySelectTask.addChild((w, e, t) -> AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -8, -5), selfProtection());

        //对话的时候，看着我的眼睛
        utilitySelectTask.addChild((w,e,t)->e.interactingPlayer!=null?0.1:0,new LookTurnToTask<>(outsider->outsider.interactingPlayer!=null?outsider.interactingPlayer.getEyePosition(0f):
                outsider.getEyePosition(0f).add(outsider.getLookVec().scale(5))));

        utilitySelectTask.addChild((w,e,t)->!e.getFollowedPlayerUUID().isPresent()?0.5:0,new SwitchActivityTask<>(()-> Activity.IDLE));
        return ImmutableList.of(new Pair<>(1, new BehaviorTreeRootTask<>(utilitySelectTask)));
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> daily()
    {
        //日常生活就是，吃饭、睡觉、打怪、发呆。
        UtilitySelectTask<OutsiderEntity> utilitySelectTask = new UtilitySelectTask<>("Daily Life");
        RandomSelectTask<OutsiderEntity>  idleTask        = new RandomSelectTask<>("Idle");

//        SynchronousTask<OutsiderEntity> randomWalk = new SynchronousTask<>("Random Walk");
//        randomWalk.addChild(LOOK_NEAREST);
//        randomWalk.addChild(new RandomWalkTask<>());

        idleTask.addChild(new LookAtNearestTask<>(),100,200);
        idleTask.addChild(new RandomWalkTask<>(), 40, 100);

        utilitySelectTask.addChild((w,e,t)->0.06,idleTask); //发呆，闲逛
        utilitySelectTask.addChild((w,e,t)->{
            long time = w.getDayTime()%24000L;
            double timeNor=time/24000D;
            return AIUtil.sigmod(timeNor,70.96d,42.58d);
        },new SleepTask<>());

        utilitySelectTask.addChild((w,e,t)->{
            GeneralFoodStats<OutsiderEntity> foodStats = e.getFoodStats();
            return AIUtil.sigmod(foodStats.getFoodLevel()/20d,-23.56, -17.4);
        },new GoapTask("eat",new EatFoodGoapGoal()));

        utilitySelectTask.addChild((w,e,t)->{
            Optional<LivingEntity> nearestHostile = e.getBrain().getMemory(MemoryModuleType.NEAREST_HOSTILE);
            if(nearestHostile.isPresent())
                return (double) (e.getHealth() / e.getMaxHealth());
            return 0d;
        },battle());

        utilitySelectTask.addChild((w,e,t)->{
            Optional<LivingEntity> nearestHostile = e.getBrain().getMemory(MemoryModuleType.NEAREST_HOSTILE);
            if(nearestHostile.isPresent())
                return (double) 1-(e.getHealth() / e.getMaxHealth()); //reverse with battle
            return 0d;
        },selfProtection());

        //对话的时候，看着我的眼睛
        utilitySelectTask.addChild((w,e,t)->e.interactingPlayer!=null?0.1:0,new LookTurnToTask<>(outsider->outsider.interactingPlayer!=null?outsider.interactingPlayer.getEyePosition(0f):
                outsider.getEyePosition(0f).add(outsider.getLookVec().scale(5))));

        utilitySelectTask.addChild((w,e,t)->e.getFollowedPlayerUUID().isPresent()?0.5:0,new SwitchActivityTask<>(()-> Activity.CORE));

        return ImmutableList.of(new Pair<>(1, new BehaviorTreeRootTask<>(utilitySelectTask)));
    }
    public static BehaviorTreeTask<OutsiderEntity> killEntityForItem(Predicate<ItemStack> predicate)
    {
        SelectorTask<OutsiderEntity> killForItemTask=new SelectorTask<>("Kill Entity For Item");
        PickItemTask<OutsiderEntity> pickItemTask = new PickItemTask<>(t->predicate.test(t.getItem()));

        SequencesTask<OutsiderEntity> attackEntityForItem = new SequencesTask<>("Attack For Item");
        ///attackEntityForItem.children.add(new FindAttackTargetTask<>();
        attackEntityForItem.addChild(new AttackTargetTask(t->AIUtil.getNearestItemProvider(t, predicate)));
        attackEntityForItem.addChild(pickItemTask);

        killForItemTask.addChild(pickItemTask); //先尝试在地上寻找
        killForItemTask.addChild(attackEntityForItem);   //如果没找到，击杀实体以获取

        return killForItemTask;
    }
    private static SequencesTask<OutsiderEntity> battle() {
        SequencesTask<OutsiderEntity> equipArmorThenBattle=new SequencesTask<>("Equip Then Battle");
        UtilitySelectTask<OutsiderEntity> battleTask = new UtilitySelectTask<>("Battle");
        battleTask.addChild((w, e, t) -> {
            return (double) (e.getHealth() / e.getMaxHealth()); //生命越多越勇
        }, new AttackTargetTask(t->t.getBrain().getMemory(MemoryModuleType.NEAREST_HOSTILE).orElse(null)));

        battleTask.addChild((w, e, t) -> {
            Optional<LivingEntity> hurtBy = e.getBrain().getMemory(MemoryModuleType.HURT_BY_ENTITY);
            if (hurtBy.isPresent())
                return 1d - e.getHealth() / e.getMaxHealth();
            return 0d;
        }, new DefendWithShieldTask());

        equipArmorThenBattle.addChild(new EquipArmorTask());
        equipArmorThenBattle.addChild(battleTask);
        return equipArmorThenBattle;
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
        SynchronousTask<OutsiderEntity> synchronousTask = new SynchronousTask<>("Heal while running");
        synchronousTask.addChild(new EscapeFromTask<>());

        UtilitySelectTask<OutsiderEntity> healSelfTask      = new UtilitySelectTask<>("Heal Self");
        SelectorTask<OutsiderEntity>      increaseSatiety = increaseSatiety();
        UsePotionTask                     usePotionTask     = new UsePotionTask(e -> e);

        SelectorTask<OutsiderEntity>      usePotionOrEatFood = new SelectorTask<>("Use Potion First"); //优先喝药
        usePotionOrEatFood.addChild(usePotionTask);
        usePotionOrEatFood.addChild(increaseSatiety);

        SelectorTask<OutsiderEntity>      eatFoodOrUsePotion = new SelectorTask<>("Eat First"); //优先吃东西
        eatFoodOrUsePotion.addChild(increaseSatiety);
        eatFoodOrUsePotion.addChild(usePotionTask);

        healSelfTask.addChild((w, e, t) -> AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -15, -5), usePotionOrEatFood); //生命值不是特别少的时候优先吃东西
        healSelfTask.addChild((w, e, t) -> e.getFoodStats().needFood()?0.8:0 * AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -8, -5), eatFoodOrUsePotion); //生命值特别少的时候优先喝药
        synchronousTask.addChild(healSelfTask);

        return synchronousTask;
    }

    public static ImmutableList<Pair<Integer, ? extends Task<? super OutsiderEntity>>> debug()
    {
/*        BehaviorTreeTask<OutsiderEntity> build = ScriptBuilder.<OutsiderEntity>start("DEBUG")
            .sync("Heal while running",
                t -> t.then(new EscapeFromTask<>()),
                t -> t.utilitySelect("heal_self",
                    p->{p.tryEach("use_potion_then_eat",
                        q-> q.then(new UsePotionTask(e->e)),
                        q-> q.then(increaseSatiety())
                        );
                        return (w,e,time)->AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -15, -5);
                    },
                    p->{p.tryEach("eat_then_use_potion",
                        q->q.then(increaseSatiety()),q->q.then(new UsePotionTask(e->e)));
                        return (w,e,time)->e.getFoodStats().needFood()?0.8:0 * AIUtil.sigmod(e.getHealth() / e.getMaxHealth(), -8, -5);
                    }))
                .build();*/

        GoapTask build=new GoapTask("eat",new EatFoodGoapGoal());

        return ImmutableList.of(new Pair<>(1, new BehaviorTreeRootTask<>(build)));
    }
}
