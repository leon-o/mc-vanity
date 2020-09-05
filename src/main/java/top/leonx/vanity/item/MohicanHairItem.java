package top.leonx.vanity.item;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.client.models.MohicanHairModel;

public class MohicanHairItem extends AbstractHairItem{
    final AbstractHairModel model;
    final RenderType renderType;
    public MohicanHairItem(Properties properties) {
        super(properties);
        setRegistryName("mohican");
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
