package top.leonx.vanity.bodypart.hair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.AbstractBodyPart;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public abstract class HairBodyPart extends AbstractBodyPart {
    ResourceLocation iconLocation;
    public HairBodyPart(BodyPartProperty property) {
        super(property);
    }

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIconLocation() {
        if(iconLocation==null)
            iconLocation=new ResourceLocation(VanityMod.MOD_ID, String.format("textures/gui/icon/%s.png", getRegistryName()));
        return iconLocation;
    }

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
