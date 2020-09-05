package top.leonx.vanity.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.BaseHairModel;

public class FringeHair extends AbstractHairItem{

    final AbstractHairModel model;
    final RenderType renderType;
    public FringeHair(Properties properties,String textureName) {
        super(properties);
        model=new BaseHairModel();
        renderType=model.getRenderType(new ResourceLocation(VanityMod.MOD_ID, "textures/model/hair/"+textureName+".png"));
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
