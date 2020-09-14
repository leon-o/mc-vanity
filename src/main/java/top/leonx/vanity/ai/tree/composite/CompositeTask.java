package top.leonx.vanity.ai.tree.composite;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;

import java.util.ArrayList;
import java.util.List;

public abstract class CompositeTask<T extends LivingEntity> extends BehaviorTreeTask<T> {
    private final List<BehaviorTreeTask<T>> children =new ArrayList<>();
    public String name;
    public CompositeTask(String name) {
        this.name=name;
    }
    public String getTaskName()
    {
        return name==null? super.getTaskName():name;
    }

    public List<BehaviorTreeTask<T>> getChildren() {
        return children;
    }
    public void addChild(BehaviorTreeTask<T> child){
        children.add(child);
    }
    public void remove(BehaviorTreeTask<T> child){
        children.remove(child);
    }
}
