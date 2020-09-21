package top.leonx.vanity.init;

import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.ai.sensor.OutsiderBedSensor;

public class ModSensorTypes {
    public static final DeferredRegister<SensorType<?>> SENSOR_TYPE = new DeferredRegister<>(ForgeRegistries.SENSOR_TYPES, VanityMod.MOD_ID);

    public static final RegistryObject<SensorType<OutsiderBedSensor>> OUTSIDER_BED_SENSOR =SENSOR_TYPE.register("outsider_bed_sensor",()->new SensorType<>(OutsiderBedSensor::new));

}
