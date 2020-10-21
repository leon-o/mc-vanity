package top.leonx.vanity.ai.goap;

import net.minecraft.item.ItemStack;
import top.leonx.vanity.ai.tree.leaf.Instantaneous.InteractTargetTask;
import top.leonx.vanity.ai.tree.leaf.continuous.AttackTargetTask;
import top.leonx.vanity.ai.tree.leaf.continuous.EatFoodTask;
import top.leonx.vanity.ai.tree.leaf.continuous.PickItemTask;
import top.leonx.vanity.ai.tree.leaf.continuous.WalkToTargetTask;
import top.leonx.vanity.util.AIUtil;

import java.util.HashSet;

public class PurposefulTaskRegistry {

    public static HashSet<PurposefulTaskWrap> purposefulTasks = new HashSet<>();

    public static void register() {
        // Eat
        registry(new PurposefulTaskWrap(new HasItemGoapGoal(ItemStack::isFood),
                                        g -> g instanceof EatFoodGoapGoal ? GoapCost.LOW : GoapCost.PASS,
                                        g -> new EatFoodTask()));

        // Pick item from ground.
        registry(new PurposefulTaskWrap(g -> new GroundItemGoapGoal(((HasItemGoapGoal) g).itemStackPredicate),
                                        goapGoal -> {
                                            if (goapGoal instanceof HasItemGoapGoal) return new GoapCost(0);
                                            return GoapCost.PASS;
                                        }, t -> new PickItemTask<>(
                p -> ((HasItemGoapGoal) t).itemStackPredicate.test(p.getItem()))));

        // Kill entity for items.
        registry(new PurposefulTaskWrap(g -> new HasEntityNearGoapGoal(
                t -> AIUtil.getLivingEntityDrops(t).stream().anyMatch(((GroundItemGoapGoal) g).itemStackPredicate)),
                                        goapGoal -> {
                                            if (goapGoal instanceof GroundItemGoapGoal) return new GoapCost(0);
                                            return GoapCost.PASS;
                                        }, g -> new AttackTargetTask(
                p -> AIUtil.getNearestItemProvider(p, ((GroundItemGoapGoal) g).itemStackPredicate))));

        // Interact with something.
        registry(new PurposefulTaskWrap(goapGoal -> new ReachTargetGoapGoal(((InteractGoapGoal) goapGoal).getPosVec()),
                                        goal -> {
                                            if (goal instanceof InteractGoapGoal) {
                                                return GoapCost.HIGH;
                                            }
                                            return GoapCost.PASS;
                                        },
                                        goapGoal -> new InteractTargetTask<>(((InteractGoapGoal) goapGoal).getPos())));


        // Move to target.
        registry(new PurposefulTaskWrap(goapGoal -> ForeverFitGoapGoal.getInstance(),
                                        goapGoal -> {
                                            if (goapGoal instanceof ReachTargetGoapGoal) {
                                                return GoapCost.LOW;
                                            }
                                            return GoapCost.PASS;
                                        },
                                        goapGoal -> new WalkToTargetTask<>(((ReachTargetGoapGoal) goapGoal).getBlockPos())));
    }

    public static void registry(PurposefulTaskWrap task) {
        purposefulTasks.add(task);
    }
}
