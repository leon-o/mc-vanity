package top.leonx.vanity.bodypart.skin;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.AbstractBodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public class SkinBodyPart extends AbstractBodyPart {
    public SkinBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.SKIN_GROUP));
    }
    ResourceLocation location;
    @Override
    public ResourceLocation getIconLocation() {
        if(location==null && getRegistryName()!=null)
            location=new ResourceLocation(VanityMod.MOD_ID, String.format("textures/gui/icon/%s.png", getRegistryName()));
        return location;
    }
    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.SKIN_COLORS;
    }
}
