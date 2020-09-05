package top.leonx.vanity.hair;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.BaseHairModel;

public class FringeHair extends VanityHair {
    final AbstractHairModel model;
    final ResourceLocation textureLocation;
    public FringeHair(ResourceLocation textureLocation, String registryName) {
        super(registryName, HairRegistry.HairType.BASE);
        model=new BaseHairModel();
        this.textureLocation=textureLocation;
    }

    @Override
    public AbstractHairModel getHairModel() {
        return model;
    }

    @Override
    public RenderType getRenderType() {
        return model.getRenderType(textureLocation);
    }
}
