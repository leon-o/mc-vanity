package top.leonx.vanity.worldgen;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModEntityTypes;

import java.util.List;
import java.util.Random;

public class TestPiece extends TemplateStructurePiece {
    private static final ResourceLocation structureLocation = new ResourceLocation("vanity", "test");

    private final ResourceLocation location;
    private final Rotation         rotation;

    public TestPiece(TemplateManager manager, ResourceLocation location, BlockPos pos, Rotation rotation, int yOffset) {
        super(ModStructurePieceTypes.TEST, 0);
        this.location = location;
        this.rotation = rotation;
        //BlockPos blockpos = BlockPos;

        this.templatePosition = new BlockPos(pos.getX(), pos.getY() - yOffset, pos.getZ());
        this.setup(manager);

    }

    public TestPiece(TemplateManager manager, CompoundNBT nbt) {
        super(ModStructurePieceTypes.TEST, nbt);
        this.location = new ResourceLocation(nbt.getString("Template"));
        this.rotation = Rotation.valueOf(nbt.getString("Rot"));
        setup(manager);
    }

    public static void init(TemplateManager templateManagerIn, BlockPos blockpos, Rotation rotation, List<StructurePiece> components, SharedSeedRandom rand, NoFeatureConfig nofeatureconfig) {
        components.add(new TestPiece(templateManagerIn, structureLocation, blockpos, rotation, 0));

//        if (rand.nextDouble() < 0.5D) {
//            int i = rand.nextInt(8) + 4;
//            components.add(new TestPiece(templateManagerIn, structureLocation, blockpos, rotation, i * 3));
//
//            for(int j = 0; j < i - 1; ++j) {
//                //components.add(new TestPiece(templateManagerIn, field_202593_f, p_207617_1_, p_207617_2_, j * 3));
//            }
//        }

        //components.add(new TestPiece(templateManagerIn, field_202592_e, p_207617_1_, p_207617_2_, 0));
    }

    @Override
    public boolean create(IWorld worldIn, ChunkGenerator<?> chunkGeneratorIn, Random randomIn, MutableBoundingBox mutableBoundingBoxIn, ChunkPos chunkPosIn) {
        int l = this.getXWithOffset(2, 5);
        int i1 = this.getYWithOffset(2);
        int k = this.getZWithOffset(2, 5);
        if (mutableBoundingBoxIn.isVecInside(new BlockPos(l, i1, k))) {
            OutsiderEntity outsiderEntity = ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.create(worldIn.getWorld());
            outsiderEntity.enablePersistence();
            outsiderEntity.setLocationAndAngles((double)l + 0.5D, i1, (double)k + 0.5D, 0.0F, 0.0F);
            outsiderEntity.onInitialSpawn(worldIn, worldIn.getDifficultyForLocation(new BlockPos(l, i1, k)), SpawnReason.STRUCTURE, null, null);
            worldIn.addEntity(outsiderEntity);
        }

        return super.create(worldIn, chunkGeneratorIn, randomIn, mutableBoundingBoxIn, chunkPosIn);
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, IWorld worldIn, Random rand, MutableBoundingBox sbb) {

    }

    protected void readAdditional(CompoundNBT tagCompound) {
        super.readAdditional(tagCompound);
        tagCompound.putString("Template", this.location.toString());
        tagCompound.putString("Rot", this.rotation.name());
    }

    private void setup(TemplateManager manager) {
        Template template = manager.getTemplateDefaulted(this.location);
        PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation).setMirror(Mirror.NONE).setCenterOffset(new BlockPos(0, 0, 0)).addProcessor(
                BlockIgnoreStructureProcessor.STRUCTURE_BLOCK);
        this.setup(template, this.templatePosition, placementsettings);
    }
}
