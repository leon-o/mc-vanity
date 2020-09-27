package top.leonx.vanity.ai.goap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class HasEntityNearGoapGoal extends GoapGoal{
    public Predicate<LivingEntity> entityPredicate;
    public HasEntityNearGoapGoal(Predicate<LivingEntity> entityPredicate) {
        super("entity_near", entity -> {
            Optional<List<LivingEntity>> livingEntities = entity.getBrain().getMemory(MemoryModuleType.VISIBLE_MOBS);
            return livingEntities.map(t->t.stream().anyMatch(entityPredicate)).orElse(false);
        });
    }
}
