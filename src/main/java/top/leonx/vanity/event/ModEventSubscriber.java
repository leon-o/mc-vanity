package top.leonx.vanity.event;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.util.PlayerSimulator;
import top.leonx.vanity.init.*;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.network.VanityEquipDataSynchronizer;
import top.leonx.vanity.network.VanityPacketHandler;
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
        ModBodyParts.register();
        ModCapabilityTypes.register();
        VanityPacketHandler.Init();
        VanityEquipDataSynchronizer.register();
        CharacterDataSynchronizer.register();
        PlayerSimulator.register();
    }


//    @OnlyIn(Dist.CLIENT)
//    @SubscribeEvent
//    public static void itemColors(ColorHandlerEvent.Item event) {
//        event.getItemColors().register(new HairItemTinter(),ModItems.ITEMS);
//    }

    @SubscribeEvent
    public static void onItemRegistry(final RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(ModItems.ITEMS);
    }
    @SubscribeEvent
    public static void onBlockRegistry(final RegistryEvent.Register<Block> event)
    {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(ModBlocks.BLOCKS);
    }

    @SubscribeEvent
    public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.registerAll(ModTileEntityTypes.VANITY_MIRROR_TILE_ENTITY_TILE_ENTITY_TYPE.setRegistryName(ModBlocks.VANITY_MIRROR.getRegistryName()));
    }
    @SubscribeEvent
    public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.registerAll(ModContainerTypes.CONTAINER_TYPES);
    }

    @SubscribeEvent
    public static void onEntityTypeRegistry(final RegistryEvent.Register<EntityType<?>> event)
    {
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        registry.register(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE);
    }

}
