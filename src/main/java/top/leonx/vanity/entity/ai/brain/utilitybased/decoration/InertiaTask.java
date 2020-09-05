package top.leonx.vanity.entity.ai.brain.utilitybased.decoration;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.entity.ai.brain.utilitybased.UtilityBasedTask;
import top.leonx.vanity.util.BinaryFunc;

public class InertiaTask<T extends LivingEntity> extends UtilityBasedTask<T> {
    BinaryFunc<Double,Double,Double> inertiaUtilityIncrement;
    long                             decayDuration;
    long                             startedTime;
    UtilityBasedTask<T> child;
    public InertiaTask(UtilityBasedTask<T> child) {
        this(child,60);
    }
    public InertiaTask(UtilityBasedTask<T> child,int decayDuration) {
        super(child.utilityScoreCalculator);
        this.decayDuration =decayDuration;
        inertiaUtilityIncrement=(elapsed, maxDuration)->{
            double a2=maxDuration*maxDuration;
            return (0.5f/a2)*(elapsed-maxDuration)*(elapsed-maxDuration);
        };
        this.child=child;
    }
    public InertiaTask(UtilityBasedTask<T> child,BinaryFunc<Double,Double,Double> inertiaFunc) {
        super(child.utilityScoreCalculator);
        inertiaUtilityIncrement=inertiaFunc;
        this.child=child;
    }

    public InertiaTask(UtilityBasedTask<T> child,BinaryFunc<Double,Double,Double> inertiaFunc, int decayDuration) {
        super(child.utilityScoreCalculator);
        inertiaUtilityIncrement=inertiaFunc;
        this.decayDuration =decayDuration;
        this.child=child;
    }


    @Override
    public void action(ServerWorld world, T entity, long executionDuration) {
        child.action(world,entity,executionDuration);
    }

    @Override
    public void ending(ServerWorld world, T entity, long executionDuration) {
        child.ending(world,entity,executionDuration);
    }


    @Override
    public Task.Status getStatus() {
        return child.getStatus();
    }

    @Override
    public double getUtilityScore(ServerWorld world, T entity, long executionDuration) {
        return child.getUtilityScore(world,entity,executionDuration)+getInertia(executionDuration);
    }

    public double getInertia(double executionDuration)
    {
        return inertiaUtilityIncrement.compute(executionDuration, (double) decayDuration);
    }

}
