package top.leonx.vanity.ai.tree;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;

public class BehaviorTreeRootTask<T extends LivingEntity> extends Task<T> {
    public BehaviorTreeTask<T> child;

    public BehaviorTreeRootTask(BehaviorTreeTask<T> child) {
        this();
        this.child = child;
    }

    public BehaviorTreeRootTask() {
        super(ImmutableMap.of(),Integer.MAX_VALUE);
    }

    long startedTime;
    @Override
    protected void startExecuting(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        startedTime=gameTimeIn;
        child.callForStart(worldIn,entityIn,0);
    }

    @Override
    protected void updateTask(ServerWorld worldIn, T owner, long gameTime) {
        child.callForUpdate(worldIn,owner,gameTime-startedTime);
    }

    @Override
    protected void resetTask(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        child.callForEnd(worldIn,entityIn,gameTimeIn-startedTime);
        startedTime=0;
    }

    @Override
    protected boolean shouldContinueExecuting(ServerWorld worldIn, T entityIn, long gameTimeIn) {
        return true;
    }

    @Override
    protected boolean shouldExecute(ServerWorld worldIn, T owner) {
        return child!=null;
    }

    @Override
    protected boolean isTimedOut(long gameTime) {
        return child!=null && child.getResult()!= BehaviorTreeTask.Result.RUNNING;
    }
}
