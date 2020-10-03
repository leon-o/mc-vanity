package top.leonx.vanity.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BedItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.ai.goap.PurposefulTaskRegistry;
import top.leonx.vanity.init.*;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.network.VanityEquipDataSynchronizer;
import top.leonx.vanity.network.VanityPacketHandler;
import top.leonx.vanity.tileentity.VanityBedTileEntity;
import top.leonx.vanity.util.PlayerSimulator;
import top.theillusivec4.curios.api.CuriosAPI;
import top.theillusivec4.curios.api.imc.CurioIMCMessage;

@Mod.EventBusSubscriber(modid = VanityMod.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventSubscriber {
    @SubscribeEvent
    public static void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo("curios", CuriosAPI.IMC.REGISTER_TYPE, () -> new CurioIMCMessage("hair").setSize(2));
        InterModComms.sendTo("curios","register_icon", ()->new Tuple<>("hair", new ResourceLocation(VanityMod.MOD_ID, "item/empty_" + "hair" + "_slot")));
    }
    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event) {
        ModCapabilityTypes.register();
        VanityPacketHandler.Init();
        VanityEquipDataSynchronizer.register();
        CharacterDataSynchronizer.register();
        PlayerSimulator.register();
        PurposefulTaskRegistry.register();
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRegisterBiome(final RegistryEvent.Register<Biome> event) {
        //noinspection deprecation
        DeferredWorkQueue.runLater(()-> ForgeRegistries.BIOMES.forEach(biome -> {
            biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES,
                             ModFeatures.TEST.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
            biome.addStructure(ModFeatures.TEST.get().withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
        }));

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRegistryPOI(final RegistryEvent.Register<PointOfInterestType> event)
    {
        for (RegistryObject<PointOfInterestType> type : ModPointOfInterest.POI_TYPE.getEntries()) {
            if(type.getId().getNamespace().equals(VanityMod.MOD_ID))
            PointOfInterestType.registerBlockStates(type.get()); //NOTICE THIS
        }
    }

//    @SubscribeEvent
//    public static void onItemRegistry(final RegistryEvent.Register<Item> event)
//    {
//        IForgeRegistry<Item> registry = event.getRegistry();
//        for (Block bedBlock : ModBlocks.getAllBedBlocks()) {
//            registry.registerAll(new BedItem(bedBlock, (new Item.Properties()).maxStackSize(1).group(ItemGroup.DECORATIONS)).setRegistryName(bedBlock.getRegistryName()));
//        }
//
//    }
    /*
//    @SubscribeEvent
//    public static void onBlockRegistry(final RegistryEvent.Register<Block> event)
//    {
//        IForgeRegistry<Block> registry = event.getRegistry();
//        registry.registerAll(ModBlocks.BLOCKS);
//    }

/*    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(ModTileEntityTypes.VANITY_MIRROR_TILE_ENTITY_TILE_ENTITY_TYPE.setRegistryName(ModBlocks.VANITY_MIRROR.block.getRegistryName()));
        registry.register(ModTileEntityTypes.PILLOW_TILE_ENTITY.setRegistryName(ModBlocks.PILLOW.block.getRegistryName()));

    }
    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.registerAll(ModContainerTypes.CONTAINER_TYPES);
    }

    @SubscribeEvent
    public static void onBodyPartRegistry(final RegistryEvent.Register<BodyPart> event)
    {
        System.out.println(event);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        registry.register(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE);

        //Biome.BIOMES.forEach(t->t.getSpawns(EntityClassification.CREATURE).add(new Biome.SpawnListEntry(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE,8,2,3)));
    }
    @SubscribeEvent
    public static void onSenserTypeRegistry(final RegistryEvent.Register<SensorType<?>> event)
    {
        IForgeRegistry<SensorType<?>> registry = event.getRegistry();
        registry.registerAll(ModSensorTypes.ALL_SENSOR_TYPES);
    }

    @SubscribeEvent
    public static void onFeatureRegistry(final RegistryEvent.Register<Feature<?>> event)
    {
        event.getRegistry().registerAll(ModFeatures.FEATURES);
    }

    @SubscribeEvent
    public static void onParticleTypeRegistry(final RegistryEvent.Register<ParticleType<?>> event)
    {
        event.getRegistry().registerAll(ModParticleTypes.GREEN_HEART.setRegistryName("green_heart"));
    }*/
//    @SubscribeEvent
//    public static void onStructurePiecesRegistry(final RegistryEvent.Register<IStructurePieceType> event)
//    {
//        event.getRegistry().registerAll(ModFeatures.FEATURES);
//    }
}
