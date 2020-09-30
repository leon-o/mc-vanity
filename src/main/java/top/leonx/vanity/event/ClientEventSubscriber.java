package top.leonx.vanity.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.particle.HeartParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.VillagerRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.client.renderer.tileentity.BedTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.ModBodyPartRenderers;
import top.leonx.vanity.client.layer.BodyPartLayer;
import top.leonx.vanity.client.renderer.VanityBedTileEntityRenderer;
import top.leonx.vanity.client.renderer.entity.OutsiderRenderer;
import top.leonx.vanity.client.screen.DialogScreen;
import top.leonx.vanity.client.screen.OutsiderInventoryScreen;
import top.leonx.vanity.client.screen.VanityMirrorScreen;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.init.ModParticleTypes;
import top.leonx.vanity.init.ModTileEntityTypes;

import java.util.Map;

@Mod.EventBusSubscriber(modid = VanityMod.MOD_ID,bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
public class ClientEventSubscriber {
    @SubscribeEvent
    public static void onFMLClientSetupEvent(final FMLClientSetupEvent event)
    {
        //noinspection deprecation
        DeferredWorkQueue.runLater(()->{
            ScreenManager.registerFactory(ModContainerTypes.VANITY_MIRROR_CONTAINER.get(), VanityMirrorScreen::new);
            ScreenManager.registerFactory(ModContainerTypes.OUTSIDER_DIALOG.get(), DialogScreen::new);
            ScreenManager.registerFactory(ModContainerTypes.OUTSIDER_INVENTORY.get(), OutsiderInventoryScreen::new);
        });

        ModBodyPartRenderers.register();

        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.get(), OutsiderRenderer::new);
        ClientRegistry.bindTileEntityRenderer(ModTileEntityTypes.VANITY_BED.get(), VanityBedTileEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onParticleFactoryRegistry(ParticleFactoryRegisterEvent event)
    {
        ParticleManager particles = Minecraft.getInstance().particles;
        particles.registerFactory(ModParticleTypes.GREEN_HEART.get(), HeartParticle.Factory::new);
        particles.registerFactory(ModParticleTypes.PINK_HEART.get(), HeartParticle.Factory::new);
    }

    @SubscribeEvent
    public static void stitchTextures(TextureStitchEvent.Pre evt) {
        if (evt.getMap().getTextureLocation() == PlayerContainer.LOCATION_BLOCKS_TEXTURE) {
            evt.addSprite(new ResourceLocation(VanityMod.MOD_ID, "item/empty_" + "hair" + "_slot"));
        }

    }

    @SubscribeEvent
    public static void postSetupClient(FMLLoadCompleteEvent evt) {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();

        for (PlayerRenderer render : skinMap.values()) {
            render.addLayer(new BodyPartLayer<>(render));
        }
        VillagerRenderer villagerRenderer = (VillagerRenderer) Minecraft.getInstance().getRenderManager().renderers.get(EntityType.VILLAGER);
        villagerRenderer.addLayer(new BodyPartLayer<>(villagerRenderer));

        ZombieRenderer zombieRenderer = (ZombieRenderer) Minecraft.getInstance().getRenderManager().renderers.get(EntityType.ZOMBIE);
        zombieRenderer.addLayer(new BodyPartLayer<>(zombieRenderer));
    }
}
