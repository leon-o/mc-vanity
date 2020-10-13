package top.leonx.vanity.bodypart.skin;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.util.Gender;

import java.util.ArrayList;
import java.util.List;

public class SkinBodyPart extends BodyPart {
    private final Gender suitableGender;
    public SkinBodyPart(Gender suitableGender) {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.SKIN_GROUP).setAvailableColors(new ArrayList<>(ColorUtil.SKIN_COLORS)));
        this.suitableGender=suitableGender;
    }

    @Override
    public Gender getSuitableGender() {
        return suitableGender;
    }
}
