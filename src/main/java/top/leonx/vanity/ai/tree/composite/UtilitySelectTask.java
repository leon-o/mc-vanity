package top.leonx.vanity.ai.tree.composite;

import com.google.common.collect.HashMultimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.antlr.v4.runtime.misc.MultiMap;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.util.BinaryFunc;
import top.leonx.vanity.util.TernaryFunc;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;


public class UtilitySelectTask<T extends LivingEntity> extends CompositeTask<T> {
    public static final UtilityScoreDecoration.NoneDecoration NONE =new UtilityScoreDecoration.NoneDecoration();
    //private final   int                                   decayDuration;
    public final  Map<BehaviorTreeTask<T>, TernaryFunc<ServerWorld, T, Long, Double>> utilityCalculatorMap      = new HashMap<>();
    public final  HashMultimap<BehaviorTreeTask<T>,UtilityScoreDecoration>                utilityScoreDecorationMap =HashMultimap.create();
    private final TernaryFunc<ServerWorld, T, Long, Double>                           DUMMY_FUNC                = (s, e, l) -> 0d;
    public        BehaviorTreeTask<T>                                                 currentTask;
    public        BehaviorTreeTask<T>                                                 lastTask;
    public UtilitySelectTask(String name) {
        super(name);
    }
    //BinaryFunc<Double, Double, Double> inertiaUtilityIncrement;

    /*public UtilitySelectTask(String name) {
        this(name);
    }*/

    /*public UtilitySelectTask(String name, int decayDuration) {
        super(name);
        this.decayDuration = decayDuration;
        inertiaUtilityIncrement = (elapsed, maxDuration) -> {
            double a2 = maxDuration * maxDuration;
            return (0.5f / a2) * (elapsed - maxDuration) * (elapsed - maxDuration);
        };
    }*/

    /*public UtilitySelectTask(String name, BinaryFunc<Double, Double, Double> inertiaUtilityIncrement, int decayDuration) {
        super(name);
        //this.inertiaUtilityIncrement = inertiaUtilityIncrement;
        this.decayDuration = decayDuration;
    }*/

    public void addChild(TernaryFunc<ServerWorld, T, Long, Double> utilityCalculator, BehaviorTreeTask<T> task) {
        addChild(utilityCalculator,task,new UtilityScoreDecoration.InertiaDecoration(0.2));
    }
    public void addChild(TernaryFunc<ServerWorld, T, Long, Double> utilityCalculator, BehaviorTreeTask<T> task,UtilityScoreDecoration ...decoration) {
        addChild(task);
        utilityCalculatorMap.put(task, utilityCalculator);
        utilityScoreDecorationMap.putAll(task, Arrays.asList(decoration));
    }
    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        if (currentTask == null) return;
        currentTask.callForEnd(world, entity, executionDuration);
        currentTask = null;
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        selectChildTask(world, entity, executionDuration);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if (currentTask != null) {
            currentTask.callForUpdate(world, entity, executionDuration);
        }
        for (Map.Entry<BehaviorTreeTask<T>, UtilityScoreDecoration> entry : utilityScoreDecorationMap.entries()) {
            entry.getValue().tick();
        }
        selectChildTask(world, entity, executionDuration);
    }

    private void selectChildTask(ServerWorld worldIn, T entityIn, long executionDuration) {
        List<Pair<Double,BehaviorTreeTask<T>>> sorted = getChildren().stream().map((BehaviorTreeTask<T> t) -> {
            Double utilityScore = utilityCalculatorMap.getOrDefault(t, DUMMY_FUNC).compute(worldIn, entityIn, executionDuration);
            for (UtilityScoreDecoration decoration : utilityScoreDecorationMap.get(t)) {
                utilityScore=decoration.decorate(utilityScore,this);
            }
//            if (Objects.equals(currentTask, t)) utilityScore += inertiaUtilityIncrement.compute((double) executionDuration, (double) decayDuration);
            return new Pair<>(utilityScore,t);
        }).sorted(Comparator.comparingDouble((ToDoubleFunction<Pair<Double, BehaviorTreeTask<T>>>) Pair::getFirst).reversed()).collect(Collectors.toList());

        for (Pair<Double,BehaviorTreeTask<T>> taskPair : sorted) {
            BehaviorTreeTask<T> task=taskPair.getSecond();

            if (task.canStart(worldIn, entityIn, executionDuration)) {
                if (currentTask != task || task.getResult() != Result.RUNNING) {
                    if (currentTask != null) currentTask.callForEnd(worldIn, entityIn, executionDuration);
                    task.callForStart(worldIn, entityIn, executionDuration);
                }

                if(currentTask!=task){
                    lastTask=currentTask;
                    currentTask = task;
                }

                break;
            }
        }
    }


}
