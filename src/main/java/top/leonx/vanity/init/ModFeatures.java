package top.leonx.vanity.init;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.worldgen.TestStructure;

public class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES =new DeferredRegister<>(ForgeRegistries.FEATURES, VanityMod.MOD_ID);


    public static final RegistryObject<Structure<NoFeatureConfig>>  TEST=FEATURES.register("test",()->new TestStructure(NoFeatureConfig::deserialize)) ;

}
