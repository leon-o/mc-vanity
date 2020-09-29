package top.leonx.vanity.bodypart.skin;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.util.Gender;

import java.util.List;

public class SkinBodyPart extends BodyPart {
    private final Gender suitableGender;
    public SkinBodyPart(Gender suitableGender) {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.SKIN_GROUP));
        this.suitableGender=suitableGender;
    }

    @Override
    public Gender getSuitableGender() {
        return suitableGender;
    }

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.SKIN_COLORS;
    }
}
