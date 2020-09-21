package top.leonx.vanity;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.init.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(VanityMod.MOD_ID)
public class VanityMod
{
    public static final String MOD_ID="vanity";
    public static final Logger LOGGER = LogManager.getLogger();

    public VanityMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModParticleTypes.PARTICLES_TYPES.register(modEventBus);
        ModSensorTypes.SENSOR_TYPE.register(modEventBus);
        ModFeatures.FEATURES.register(modEventBus);
        ModPointOfInterest.POI_TYPE.register(modEventBus);

        modEventBus.addListener(BodyPartRegistry::initBodyParts);
    }
}
