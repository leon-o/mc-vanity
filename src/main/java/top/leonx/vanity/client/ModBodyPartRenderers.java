package top.leonx.vanity.client;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.client.models.*;
import top.leonx.vanity.client.renderer.bodypart.EyeBodyPartRender;
import top.leonx.vanity.client.renderer.bodypart.HairBodyPartRenderer;
import top.leonx.vanity.client.renderer.bodypart.MouthBodyPartRenderer;
import top.leonx.vanity.client.renderer.bodypart.SkinBodyPartRenderer;
import top.leonx.vanity.init.ModBodyParts;

public class ModBodyPartRenderers {
    private static ResourceLocation getHairLocation(String name)
    {
        return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/model/hair/%s.png", name));
    }
    private static ResourceLocation getEyeLocation(String name)
    {
        return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/model/eye/%s.png", name));
    }
    private static ResourceLocation getSkinLocation(String name){return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/model/skin/%s.png", name));}
    private static ResourceLocation getMouthLocation(String name){return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/model/mouth/%s.png", name));}
    public static void register()
    {
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_1, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_1.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_2, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_2.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.PONYTAIL, new HairBodyPartRenderer(new PonytailModel(), getHairLocation(ModBodyParts.PONYTAIL.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.DOUBLE_PONYTAIL, new HairBodyPartRenderer(new DoublePonytailModel(), getHairLocation(ModBodyParts.DOUBLE_PONYTAIL.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.LONG_DOUBLE_PONYTAIL, new HairBodyPartRenderer(new LongDoublePonytailModel(), getHairLocation("ponytail")));
        BodyPartRendererRegistry.register(ModBodyParts.LONG_HAIR, new HairBodyPartRenderer(new LongHairModel(), getHairLocation(ModBodyParts.LONG_HAIR.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.MOHICAN, new HairBodyPartRenderer(new MohicanHairModel(), getHairLocation(ModBodyParts.MOHICAN.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_3, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_3.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_4, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_4.getRegistryName())));
        //BodyPartRendererRegistry.register(ModBodyParts.EYE_DEBUG, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_DEBUG.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_1, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_1.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_2, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_2.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_3, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_3.getRegistryName())));

        BodyPartRendererRegistry.register(ModBodyParts.SKIN_FEMALE_1, new SkinBodyPartRenderer(getSkinLocation(ModBodyParts.SKIN_FEMALE_1.getRegistryName())));

        //BodyPartRendererRegistry.register(ModBodyParts.MOUTH_DEBUG,new MouthBodyPartRenderer(getMouthLocation(ModBodyParts.MOUTH_DEBUG.getRegistryName())));
        BodyPartRendererRegistry.register(ModBodyParts.MOUTH_1,new MouthBodyPartRenderer(getMouthLocation(ModBodyParts.MOUTH_1.getRegistryName())));

    }
}
