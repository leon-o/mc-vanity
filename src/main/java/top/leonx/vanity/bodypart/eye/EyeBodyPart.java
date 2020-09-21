package top.leonx.vanity.bodypart.eye;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public class EyeBodyPart extends BodyPart {
    public EyeBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EYE_GROUP).addFloat("eye_height",-0.175f,0.175f,0));
    }
    ResourceLocation location;

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
