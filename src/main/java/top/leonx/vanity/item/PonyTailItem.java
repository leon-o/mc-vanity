package top.leonx.vanity.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.PonytailModel;

public class PonyTailItem extends AbstractHairItem {
    final AbstractHairModel model;
    final RenderType renderType;
    public PonyTailItem(Properties properties) {
        super(properties);
        model = new PonytailModel();
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
