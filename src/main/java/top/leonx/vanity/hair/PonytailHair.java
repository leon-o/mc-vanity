package top.leonx.vanity.hair;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.PonytailModel;

public class PonytailHair extends ExtraHair{
    AbstractHairModel model;
    RenderType renderType;
    public PonytailHair() {
        super("ponytail");
        model=new PonytailModel();
        renderType=model.getRenderType(new ResourceLocation(VanityMod.MOD_ID, "textures/model/hair/ponytail.png"));
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
