package top.leonx.vanity.hair;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.MohicanHairModel;

public class MohicanHair extends ExtraHair {
    final AbstractHairModel model;
    final RenderType renderType;
    public MohicanHair() {
        super("mohican");
        model=new MohicanHairModel();
        renderType=model.getRenderType(new ResourceLocation(VanityMod.MOD_ID, "textures/model/hair/mohican.png"));
    }

    @Override
    public AbstractHairModel getHairModel() {
        return model;
    }

    @Override
    public RenderType getRenderType() {
        return renderType;
    }
}
