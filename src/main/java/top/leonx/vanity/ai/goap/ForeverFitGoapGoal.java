package top.leonx.vanity.ai.goap;

public class ForeverFitGoapGoal extends GoapGoal{
    private static ForeverFitGoapGoal instance;

    public static ForeverFitGoapGoal getInstance() {
        if(instance==null)
            instance=new ForeverFitGoapGoal();
        return instance;
    }

    public ForeverFitGoapGoal() {
        super("forever_fit",e->true);
    }
}
