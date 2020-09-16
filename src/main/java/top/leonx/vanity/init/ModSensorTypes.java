package top.leonx.vanity.init;

import net.minecraft.entity.ai.brain.sensor.SensorType;
import top.leonx.vanity.ai.sensor.OutsiderBedSensor;

public class ModSensorTypes {
    public static final SensorType<OutsiderBedSensor> OUTSIDER_BED_SENSOR =new SensorType<>(OutsiderBedSensor::new);

    public static final SensorType<?>[] ALL_SENSOR_TYPES ={
            OUTSIDER_BED_SENSOR.setRegistryName("outsider_bed_sensor")
    };
}
