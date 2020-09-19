package top.leonx.vanity.worldgen;

import com.mojang.datafixers.Dynamic;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.feature.template.TemplateManager;
import top.leonx.vanity.init.ModFeatures;

import java.util.Random;
import java.util.function.Function;

public class TestStructure extends ScatteredStructure<NoFeatureConfig> {
    public TestStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> configFactoryIn) {
        super(configFactoryIn);
    }

    @Override
    public IStartFactory getStartFactory() {
        return TestStructure.Start::new;
    }

    @Override
    public String getStructureName() {
        return "test";
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    protected int getSeedModifier() {
        return 14357621;
    }


    public static class Start extends StructureStart {

        public Start(Structure<?> structure, int chunkX, int chunkZ, MutableBoundingBox boundingBox, int reference, long seed) {
            super(structure, chunkX, chunkZ, boundingBox, reference, seed);
        }

        @Override
        public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn) {
            NoFeatureConfig nofeatureconfig = generator.getStructureConfig(biomeIn, ModFeatures.TEST);//todo
            int             blockX          = chunkX * 16;
            int             blockZ          = chunkZ * 16;
            int             height          = generator.getHeight(blockX, blockZ, Heightmap.Type.WORLD_SURFACE);
            BlockPos        blockpos        = new BlockPos(blockX, height, blockZ);
            Rotation        rotation        = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

            TestPiece.init(templateManagerIn, blockpos, rotation, this.components, this.rand, nofeatureconfig);
            recalculateStructureSize();
        }
    }
}
