package top.leonx.vanity.entity.ai.brain.utilitybased;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UtilitySelectTask<T extends LivingEntity> extends Task<T> {
    public List<UtilityBasedTask<T>> children                = new ArrayList<>();
    public UtilityBasedTask<T> currentTask;
    public long                currentTaskStartedTime;
    public UtilitySelectTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
        super(requiredMemoryStateIn);
    }

    public void addChild(UtilityBasedTask<T> task) {
        children.add(task);
    }

    @Override
    protected void startExecuting(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        currentTaskStartedTime =gameTimeIn;
        selectChildTask(worldIn,entityIn,gameTimeIn);
    }

    @Override
    protected void updateTask(ServerWorld worldIn, T owner, long gameTime) {
        if(currentTask!=null)
            currentTask.action(worldIn, owner, gameTime- currentTaskStartedTime);


        selectChildTask(worldIn,owner,gameTime);
    }

    @Override
    protected void resetTask(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        super.resetTask(worldIn, entityIn, gameTimeIn);
        if(currentTask==null) return;
        currentTask.callForEnd(worldIn,entityIn,gameTimeIn);
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, T entityIn, long gameTimeIn)
    {
        return true;
    }

    private void selectChildTask(ServerWorld worldIn, T entityIn, long gameTimeIn)
    {
        List<UtilityBasedTask<T>> sorted = children.stream().sorted(
                Comparator.comparingDouble(t -> ((UtilityBasedTask<T>) t).getUtilityScore(worldIn, entityIn, gameTimeIn - currentTaskStartedTime)).reversed()).collect(Collectors.toList());

        for (UtilityBasedTask<T> task : sorted) {
            if(task.canStart(worldIn,entityIn,gameTimeIn))
            {
                currentTask=task;
                currentTaskStartedTime =gameTimeIn;
                break;
            }
        }
    }
}
