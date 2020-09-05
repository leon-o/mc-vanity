package top.leonx.vanity.bodypart.eye;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.AbstractBodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public class EyeBodyPart extends AbstractBodyPart {
    public EyeBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EYE_GROUP).addFloat("eye_height",-0.175f,0.175f,0));
    }
    ResourceLocation location;

    public ResourceLocation getIconLocation() {
        if(location==null && getRegistryName()!=null)
            location=new ResourceLocation(VanityMod.MOD_ID, String.format("textures/gui/icon/%s.png", getRegistryName()));
        return location;
    }
    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
