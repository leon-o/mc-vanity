package top.leonx.vanity.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.DoublePonytailModel;

public class DoublePonyTailItem extends AbstractHairItem {
    final AbstractHairModel model;
    final RenderType renderType;
    public DoublePonyTailItem(Properties properties) {
        super(properties);
        setRegistryName("double_ponytail");
        model = new DoublePonytailModel();
        renderType=model.getRenderType(new ResourceLocation(VanityMod.MOD_ID, "textures/model/hair/double_ponytail.png"));
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
