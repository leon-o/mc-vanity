package top.leonx.vanity.bodypart.hair;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.util.Gender;

import java.util.List;

public abstract class HairBodyPart extends BodyPart {
    private final Gender suitableGender;
    public HairBodyPart(BodyPartProperty property, Gender suitable) {
        super(property);
        this.suitableGender=suitable;
    }

    @Override
    public Gender getSuitableGender() {
        return suitableGender;
    }

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
