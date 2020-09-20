package top.leonx.vanity.ai.tree.composite;

import javafx.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.util.BinaryFunc;
import top.leonx.vanity.util.TernaryFunc;

import java.util.*;
import java.util.stream.Collectors;


public class UtilitySelectTask<T extends LivingEntity> extends CompositeTask<T> {
    public static final UtilityScoreDecoration.NoneDecoration NONE =new UtilityScoreDecoration.NoneDecoration();
    //private final   int                                   decayDuration;
    public final Map<BehaviorTreeTask<T>, TernaryFunc<ServerWorld, T, Long, Double>> utilityCalculatorMap = new HashMap<>();
    public final Map<BehaviorTreeTask<T>,UtilityScoreDecoration> utilityScoreDecorationMap=new HashMap<>();
    private final TernaryFunc<ServerWorld, T, Long, Double>                           DUMMY_FUNC           = (s, e, l) -> 0d;
    public        BehaviorTreeTask<T>                                                 currentTask;

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
    public void addChild(TernaryFunc<ServerWorld, T, Long, Double> utilityCalculator, BehaviorTreeTask<T> task,UtilityScoreDecoration decoration) {
        addChild(task);
        utilityCalculatorMap.put(task, utilityCalculator);
        utilityScoreDecorationMap.put(task,decoration);
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
        selectChildTask(world, entity, executionDuration);
    }

    private void selectChildTask(ServerWorld worldIn, T entityIn, long executionDuration) {
        List<BehaviorTreeTask<T>> sorted = getChildren().stream().sorted(Comparator.comparingDouble((t) -> {
            Double utilityScore = utilityCalculatorMap.getOrDefault(t, DUMMY_FUNC).compute(worldIn, entityIn, executionDuration);
            utilityScore = utilityScoreDecorationMap.getOrDefault(t,NONE).decorate(utilityScore,this);
//            if (Objects.equals(currentTask, t)) utilityScore += inertiaUtilityIncrement.compute((double) executionDuration, (double) decayDuration);
            return utilityScore;
        }).reversed()).collect(Collectors.toList());

        for (BehaviorTreeTask<T> task : sorted) {
            if (task.canStart(worldIn, entityIn, executionDuration)) {
                if (currentTask != task || task.getResult() != Result.RUNNING) {
                    if (currentTask != null) currentTask.callForEnd(worldIn, entityIn, executionDuration);
                    task.callForStart(worldIn, entityIn, executionDuration);
                }

                currentTask = task;
                break;
            }
        }
    }


}
