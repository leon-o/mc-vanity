package top.leonx.vanity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Tuple;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.ai.tree.composite.*;
import top.leonx.vanity.util.TernaryFunc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Example ScriptBuilder.start("name").then("name",doSth).branch(
 *      builder->builder.random(
 *              t->t.then().then(),
 *              t->t.random(
 *                  y->{t.then().then(); return new Tuple<>(60,180)},
 *                  y->t.then(),
 *                  ),
 *              t->t.then().branch(
 *                      y->y.jumpTo("dada"),
 *                      y->y.then(),
 *                  ),
 *              t->t.
 *          ),
 *      builder->builder.jumpTo()
 *      );
 * @param <T>
 */
public class ScriptBuilder<T extends LivingEntity> {
    CompositeTaskTree<? extends BehaviorTreeTask<T>> taskTreeRoot;
    //TaskTree currentNode;

    public ScriptBuilder(String name) {
        this.taskTreeRoot = new CompositeTaskTree<>(name, () -> new SequencesTask<>(name));
        //currentNode=taskTreeRoot;
    }

    public static <T extends LivingEntity> ScriptBuilder<T> start(String name) {

        return new ScriptBuilder<>(name);
    }

    @SuppressWarnings("UnusedReturnValue")
    public ScriptBuilder<T> then(BehaviorTreeTask<T> task) {
        taskTreeRoot.children.add(new LeafTaskTree(task));
        return this;
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public ScriptBuilder<T> tryEach(String name, Consumer<ScriptBuilder<T>>... builders) {
        CompositeTaskTree<SelectorTask<T>> tree    = new CompositeTaskTree<>(name, () -> new SelectorTask<>(name));
        ScriptBuilder<T>                   builder = createFrom(tree);
        for (Consumer<ScriptBuilder<T>> supplier : builders) {
            supplier.accept(builder);
        }
        this.taskTreeRoot.children.add(tree);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ScriptBuilder<T> sync(String name, Consumer<ScriptBuilder<T>>... builders) {
        CompositeTaskTree<SynchronousTask<T>> tree    = new CompositeTaskTree<>(name, () -> new SynchronousTask<>(name));
        ScriptBuilder<T>                      builder = createFrom(tree);
        for (Consumer<ScriptBuilder<T>> supplier : builders) {
            supplier.accept(builder);
        }
        this.taskTreeRoot.children.add(tree);
        return this;
    }

    @SuppressWarnings("unchecked")
    public ScriptBuilder<T> random(String name, Function<ScriptBuilder<T>, Tuple<Integer, Integer>>... builders) {
        RandomTaskTree   tree    = new RandomTaskTree(name);
        ScriptBuilder<T> builder = createFrom(tree);
        for (Function<ScriptBuilder<T>, Tuple<Integer, Integer>> supplier : builders) {
            Tuple<Integer, Integer> duration = supplier.apply(builder);
            tree.durationList.add(duration);
        }
        this.taskTreeRoot.children.add(tree);
        return this;
    }

    @SuppressWarnings("unused")
    public void jumpTo(String targetName) {
        taskTreeRoot.children.add(new JumpTaskTree(targetName));
    }

    @SuppressWarnings({"unchecked", "UnusedReturnValue"})
    public ScriptBuilder<T> utilitySelect(String name, Function<ScriptBuilder<T>, TernaryFunc<ServerWorld, T, Long, Double>>... builders) {
        UtilityTaskTree  utilityTaskTree = new UtilityTaskTree(name);
        ScriptBuilder<T> builder         = createFrom(utilityTaskTree);
        for (Function<ScriptBuilder<T>, TernaryFunc<ServerWorld, T, Long, Double>> supplier : builders) {
            TernaryFunc<ServerWorld, T, Long, Double> calculator = supplier.apply(builder);
            utilityTaskTree.utilityCalculatorList.add(calculator);
        }
        this.taskTreeRoot.children.add(utilityTaskTree);
        return this;
    }
    @SuppressWarnings("unused")
    public ScriptBuilder<T> branch(String name, Consumer<ScriptBuilder<T>> whenSuccess, Consumer<ScriptBuilder<T>> whenFail)
    {
        BranchTaskTree branchTaskTree=new BranchTaskTree(name);
        AbstractTaskTree lastTreeNode = taskTreeRoot.children.get(taskTreeRoot.children.size() - 1);
        branchTaskTree.setCondition(lastTreeNode);
        ScriptBuilder<T> whenSuccessBuilder = new ScriptBuilder<>(name + "_success");
        ScriptBuilder<T> whenFailBuilder = new ScriptBuilder<>(name + "_fail");
        whenSuccess.accept(whenSuccessBuilder);
        whenFail.accept(whenFailBuilder);

        branchTaskTree.setWhenSuccess(whenSuccessBuilder.taskTreeRoot);
        branchTaskTree.setWhenFail(whenFailBuilder.taskTreeRoot);

        taskTreeRoot.children.set(taskTreeRoot.children.size() - 1,lastTreeNode);

        return this;
    }
    public BehaviorTreeTask<T> build() {
        return taskTreeRoot.build(this);
    }

    private ScriptBuilder<T> createFrom(CompositeTaskTree<? extends BehaviorTreeTask<T>> tree) {
        ScriptBuilder<T> builder = new ScriptBuilder<>(tree.nodeName);
        builder.taskTreeRoot = tree;
        return builder;
    }

    private abstract class AbstractTaskTree {
        boolean             locked;
        BehaviorTreeTask<T> product;

        @Nullable
        public BehaviorTreeTask<T> build(ScriptBuilder<T> rootBuilder) {
            locked=true;
            if (product == null) {
                product = buildInternal(rootBuilder);
            }
            return product;
        }

        @Nullable
        public abstract BehaviorTreeTask<T> buildInternal(ScriptBuilder<T> rootBuilder);
    }

    private class LeafTaskTree extends AbstractTaskTree {
        BehaviorTreeTask<T> task;

        public LeafTaskTree(BehaviorTreeTask<T> task) {
            this.task = task;
        }

        @Override
        public BehaviorTreeTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            return task;
        }
    }

    private class CompositeTaskTree<M extends CompositeTask<T>> extends AbstractTaskTree {
        String                 nodeName;
        Supplier<M>            compositeTaskSupplier;
        List<AbstractTaskTree> children= new ArrayList<>();

        public CompositeTaskTree(String nodeName, Supplier<M> compositorGetter) {
            this.nodeName = nodeName;
            compositeTaskSupplier = compositorGetter;
        }

        @Override
        public BehaviorTreeTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            if(children.size()==1)
                return children.get(0).build(rootBuilder);
            else if(children.size()==0)
                return null;

            M root = compositeTaskSupplier.get();
            for (AbstractTaskTree child : children) {
                root.addChild(child.buildInternal(rootBuilder));
            }
            return root;
        }

        @Nullable
        public CompositeTaskTree<M> findChild(String nodeName) {
            for (AbstractTaskTree child : children) {
                if (child instanceof CompositeTaskTree) {
                    CompositeTaskTree<M> compositeChild = (CompositeTaskTree<M>) child;
                    if (compositeChild.nodeName.equals(nodeName)) return compositeChild;
                    else {
                        CompositeTaskTree<M> findedChild = compositeChild.findChild(nodeName);
                        if (findedChild != null) return findedChild;
                    }
                }
            }
            return null;
        }
    }

    private class RandomTaskTree extends CompositeTaskTree<RandomSelectTask<T>> {
        public List<Tuple<Integer, Integer>> durationList = new ArrayList<>();

        public RandomTaskTree(String nodeName) {
            super(nodeName, () -> new RandomSelectTask<>(nodeName));
        }

        @Override
        public RandomSelectTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            RandomSelectTask<T> root = compositeTaskSupplier.get();
            for (int i = 0; i < children.size(); i++) {
                AbstractTaskTree        child    = children.get(i);
                Tuple<Integer, Integer> duration = durationList.get(i);
                root.addChild(child.buildInternal(rootBuilder), duration.getA(), duration.getB());
            }
            return root;
        }
    }

    public class UtilityTaskTree extends CompositeTaskTree<UtilitySelectTask<T>> {
        public List<TernaryFunc<ServerWorld, T, Long, Double>> utilityCalculatorList = new ArrayList<>();

        public UtilityTaskTree(String nodeName) {
            super(nodeName, () -> new UtilitySelectTask<>(nodeName));
        }

        @Override
        public UtilitySelectTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            UtilitySelectTask<T> root = compositeTaskSupplier.get();
            for (int i = 0; i < children.size(); i++) {
                AbstractTaskTree                          child      = children.get(i);
                TernaryFunc<ServerWorld, T, Long, Double> calculator = utilityCalculatorList.get(i);
                root.addChild(calculator, child.buildInternal(rootBuilder));
            }
            return root;
        }
    }

    private class JumpTaskTree extends AbstractTaskTree {
        public String targetName;

        public JumpTaskTree(String targetName) {
            this.targetName = targetName;
        }

        @Override
        public CompositeTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            CompositeTaskTree<? extends CompositeTask<T>> targetNode = rootBuilder.taskTreeRoot.findChild(targetName);
            if (targetNode != null) return (CompositeTask<T>) targetNode.build(rootBuilder);
            return null;
        }
    }

    private class BranchTaskTree extends CompositeTaskTree<BranchTask<T>>{
        public String name;
        public void setCondition(AbstractTaskTree taskTree)
        {
            children.set(0,taskTree);
        }
        public void setWhenSuccess(AbstractTaskTree taskTree)
        {
            children.set(1,taskTree);
        }
        public void setWhenFail(AbstractTaskTree taskTree)
        {
            children.set(2,taskTree);
        }
        public BranchTaskTree(String nodeName) {
            super(nodeName, ()->new BranchTask<>(nodeName));
        }

        @Nullable
        @Override
        public BranchTask<T> buildInternal(ScriptBuilder<T> rootBuilder) {
            if(children.size()==3)
            {
                BranchTask<T> branchTask=new BranchTask<>(name);
                branchTask.condition=children.get(0).build(rootBuilder);
                branchTask.whenSuccess=children.get(1).build(rootBuilder);
                branchTask.whenFail=children.get(2).build(rootBuilder);
                return branchTask;
            }
            return null;
        }
    }
}
