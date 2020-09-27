package top.leonx.vanity.ai.goap;

public class EatFoodGoapGoal extends GoapGoal{
    public EatFoodGoapGoal() {
        super("eat_food", t->!t.getFoodStats().needFood());
    }
}
