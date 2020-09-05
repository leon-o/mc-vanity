package top.leonx.vanity.hair;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.LongDoublePonytailModel;

public class LongDoublePonyTail extends ExtraHair {
    final AbstractHairModel model;
    final RenderType renderType;
    public LongDoublePonyTail() {
        super("long_double_ponytail");
        model = new LongDoublePonytailModel();
        renderType=model.getRenderType(new ResourceLocation(VanityMod.MOD_ID, "textures/model/hair/ponytail.png"));
    }


    public AbstractHairModel getHairModel() {
        return model;
    }


    @Override
    public RenderType getRenderType() {
        return renderType;
    }

}
