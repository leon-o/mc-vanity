package top.leonx.vanity.ai.tree.composite;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.ai.tree.IUtilityBased;
import top.leonx.vanity.util.BinaryFunc;
import top.leonx.vanity.util.TernaryFunc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class UtilitySelectTask<T extends LivingEntity> extends BehaviorTreeTask<T> {
    private final int decayDuration;
    public List<Pair<TernaryFunc<ServerWorld,T,Long,Double>,BehaviorTreeTask<T>>> children = new ArrayList<>();
    public BehaviorTreeTask<T>                                                    currentTask;
    BinaryFunc<Double,Double,Double> inertiaUtilityIncrement;

    public UtilitySelectTask()
    {
        this(60);
    }
    public UtilitySelectTask(int decayDuration) {
        this.decayDuration =decayDuration;
        inertiaUtilityIncrement=(elapsed, maxDuration)->{
            double a2=maxDuration*maxDuration;
            return (0.5f/a2)*(elapsed-maxDuration)*(elapsed-maxDuration);
        };
    }

    public UtilitySelectTask(BinaryFunc<Double, Double, Double> inertiaUtilityIncrement,int decayDuration) {
        this.inertiaUtilityIncrement = inertiaUtilityIncrement;
        this.decayDuration=decayDuration;
    }

    public void addChild(TernaryFunc<ServerWorld,T,Long,Double> utilityCalculator, BehaviorTreeTask<T> task) {
        children.add(new Pair<>(utilityCalculator,task));
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        selectChildTask(world,entity,executionDuration);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(currentTask!=null) {
            currentTask.callForUpdate(world, entity, executionDuration);
        }
        selectChildTask(world,entity,executionDuration);
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        if(currentTask==null) return;
        currentTask.callForEnd(world,entity,executionDuration);
        currentTask=null;
    }



    private void selectChildTask(ServerWorld worldIn, T entityIn, long executionDuration)
    {
        List<BehaviorTreeTask<T>> sorted = children.stream().sorted(
                Comparator.comparingDouble((Pair<TernaryFunc<ServerWorld,T,Long,Double>,BehaviorTreeTask<T>> t) -> {
                    Double utilityScore = t.getFirst().compute(worldIn, entityIn, executionDuration);
                    if(Objects.equals(currentTask, t.getSecond()))
                        utilityScore+=inertiaUtilityIncrement.compute((double)executionDuration,(double)decayDuration);
                    return utilityScore;
                }).reversed()).map(Pair::getSecond).collect(Collectors.toList());

        for (BehaviorTreeTask<T> task : sorted) {
            if(task.canStart(worldIn,entityIn,executionDuration))
            {
                if(currentTask!=task || task.getResult()!=Result.RUNNING){
                    if(currentTask!=null)
                        currentTask.callForEnd(worldIn, entityIn, executionDuration);
                    task.callForStart(worldIn, entityIn, executionDuration);
                }

                currentTask=task;
                break;
            }
        }
    }
}
