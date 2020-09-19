package top.leonx.vanity.init;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import top.leonx.vanity.worldgen.TestStructure;

public class ModFeatures {
    public static final Structure<NoFeatureConfig> TEST=new TestStructure(NoFeatureConfig::deserialize);

    public static final Feature<?>[] FEATURES ={
            TEST.setRegistryName("test")
    };
}
