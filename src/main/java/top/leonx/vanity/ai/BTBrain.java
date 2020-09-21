package top.leonx.vanity.ai;

import com.mojang.datafixers.Dynamic;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;

import java.util.Collection;

public class BTBrain<T extends LivingEntity> extends Brain<T> {
    public <T1> BTBrain(Collection<MemoryModuleType<?>> memoryModules, Collection<SensorType<? extends Sensor<? super T>>> sensorTypes, Dynamic<T1> dynamicIn) {
        super(memoryModules, sensorTypes, dynamicIn);
    }


}
