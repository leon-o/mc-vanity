package top.leonx.vanity.entity.ai.brain.utilitybased.composite;

import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Map;

public class SequencesTask extends Task<OutsiderEntity> {
    public SequencesTask(Map<MemoryModuleType<?>, MemoryModuleStatus> requiredMemoryStateIn) {
        super(requiredMemoryStateIn);
    }

}
