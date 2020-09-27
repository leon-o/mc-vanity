package top.leonx.vanity.ai.goap;

import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.function.Function;

public class PurposefulTaskWrap {
    /**
     * Convert purpose to precondition
     */
    public Function<GoapGoal,GoapGoal> precondition;

    /**
     * Handle others precondition and check if we can solve that.
     */
    public Function<GoapGoal,GoapCost> goapGoalHandler;

    /**
     * Create task from the purpose.
     */
    public Function<GoapGoal,BehaviorTreeTask<OutsiderEntity>> task;

    public PurposefulTaskWrap(GoapGoal precondition, Function<GoapGoal, GoapCost> goapGoalHandler, Function<GoapGoal,BehaviorTreeTask<OutsiderEntity>> task)
    {
        this(t->precondition,goapGoalHandler,task);
    }
    public PurposefulTaskWrap(Function<GoapGoal,GoapGoal> precondition, Function<GoapGoal, GoapCost> goapGoalHandler, Function<GoapGoal,BehaviorTreeTask<OutsiderEntity>> task) {
        this.precondition = precondition;
        this.goapGoalHandler = goapGoalHandler;
        this.task = task;
    }


    public GoapCost handleGoapGoal(GoapGoal goapGoal)
    {
        return goapGoalHandler.apply(goapGoal);
    }
}
