package top.leonx.vanity.bodypart.skin;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public class SkinBodyPart extends BodyPart {
    public SkinBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.SKIN_GROUP));
    }

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.SKIN_COLORS;
    }
}
