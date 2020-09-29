package top.leonx.vanity.client;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.client.models.*;
import top.leonx.vanity.client.renderer.bodypart.*;
import top.leonx.vanity.init.ModBodyParts;

import java.io.IOException;

public class ModBodyPartRenderers {
    private static ResourceLocation getHairLocation(String name)
    {
        return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/bodypart/hair/%s.png", name));
    }
    private static ResourceLocation getEyeLocation(String name)
    {
        return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/bodypart/eye/%s.png", name));
    }
    private static ResourceLocation getSkinLocation(String name){return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/bodypart/skin/%s.png", name));}
    private static ResourceLocation getMouthLocation(String name){return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/bodypart/mouth/%s.png", name));}
    private static ResourceLocation getDressLocation(String name){return new ResourceLocation(VanityMod.MOD_ID, String.format("textures/bodypart/dress/%s.png", name));}

    public static void register()
    {
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_1, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_1.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_2, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_2.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.PONYTAIL, new HairBodyPartRenderer(new PonytailModel(), getHairLocation(ModBodyParts.PONYTAIL.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.DOUBLE_PONYTAIL, new HairBodyPartRenderer(new DoublePonytailModel(), getHairLocation(ModBodyParts.DOUBLE_PONYTAIL.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.LONG_DOUBLE_PONYTAIL, new HairBodyPartRenderer(new LongDoublePonytailModel(), getHairLocation("ponytail")));
        BodyPartRendererRegistry.register(ModBodyParts.LONG_HAIR, new HairBodyPartRenderer(new LongHairModel(), getHairLocation(ModBodyParts.LONG_HAIR.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.MOHICAN, new HairBodyPartRenderer(new MohicanHairModel(), getHairLocation(ModBodyParts.MOHICAN.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_3, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_3.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.FRINGE_4, new HairBodyPartRenderer(new BaseHairModel(), getHairLocation(ModBodyParts.FRINGE_4.get().getName())));
        //BodyPartRendererRegistry.register(ModBodyParts.EYE_DEBUG, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_DEBUG.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_1, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_1.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_2, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_2.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.EYE_3, new EyeBodyPartRender(getEyeLocation(ModBodyParts.EYE_3.get().getName())));

        BodyPartRendererRegistry.register(ModBodyParts.SKIN_FEMALE_1, new SkinBodyPartRenderer(getSkinLocation(ModBodyParts.SKIN_FEMALE_1.get().getName()),true));
        BodyPartRendererRegistry.register(ModBodyParts.SKIN_MALE_1, new SkinBodyPartRenderer(getSkinLocation(ModBodyParts.SKIN_MALE_1.get().getName()),false));

        //BodyPartRendererRegistry.register(ModBodyParts.MOUTH_DEBUG,new MouthBodyPartRenderer(getMouthLocation(ModBodyParts.MOUTH_DEBUG.get().getName())));
        BodyPartRendererRegistry.register(ModBodyParts.MOUTH_1,new MouthBodyPartRenderer(getMouthLocation(ModBodyParts.MOUTH_1.get().getName())));
        try {
            BodyPartRendererRegistry.register(ModBodyParts.DRESS_DEBUG,new DressBodyPartRenderer(new DressObjModel(new ResourceLocation(VanityMod.MOD_ID, "models/bodypart/dress.obj"),false) ,
                                                                                                 getDressLocation(ModBodyParts.DRESS_DEBUG.get().getName())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
