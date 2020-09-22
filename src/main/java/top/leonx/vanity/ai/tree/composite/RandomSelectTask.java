package top.leonx.vanity.ai.tree.composite;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.HashMap;
import java.util.Map;

public class RandomSelectTask<T extends LivingEntity> extends CompositeTask<T>{
    // Pair<Min Duration,Max Duration>
    public Map<BehaviorTreeTask<T>, Pair<Integer,Integer>> durationMap=new HashMap<>();
    public BehaviorTreeTask<T> currentTask;
    public long                currentTaskStopTime=0;
    public RandomSelectTask(String name) {
        super(name);
    }
    public void addChild(BehaviorTreeTask<T> child,int minDuration,int maxDuration)
    {
        super.addChild(child);
        durationMap.put(child,new Pair<>(minDuration,maxDuration));
    }

    @Override
    public void addChild(BehaviorTreeTask<T> child) {
        addChild(child,60,60);
    }

    @Override
    protected void onStart(ServerWorld world, T entity, long executionDuration) {
        randomStart(world, entity, executionDuration);
    }

    @Override
    protected void onUpdate(ServerWorld world, T entity, long executionDuration) {
        if(currentTask!=null)
            currentTask.callForUpdate(world, entity, executionDuration);
        if(executionDuration>currentTaskStopTime || currentTask==null || currentTask.getResult()!=Result.RUNNING)
            randomStart(world, entity, executionDuration);
    }

    @Override
    protected void onEnd(ServerWorld world, T entity, long executionDuration) {
        if(currentTask!=null)
            currentTask.callForEnd(world, entity, executionDuration);
        currentTaskStopTime=0;
    }

    private void randomStart(ServerWorld world, T entity, long executionDuration)
    {
        if(getChildren().size()==0){
            submitResult(Result.FAIL);
            return;
        }
        if(currentTask!=null)
            currentTask.callForEnd(world,entity,executionDuration);

        currentTask=getChildren().get(entity.getRNG ().nextInt((getChildren().size())));
        Pair<Integer, Integer> durationPair = durationMap.get(currentTask);
        int minDuration=durationPair.getFirst();
        int maxDuration=durationPair.getSecond();
        currentTaskStopTime = executionDuration+entity.getRNG().nextInt(maxDuration-minDuration)+minDuration;
        currentTask.callForStart(world,entity,executionDuration);
    }
}
