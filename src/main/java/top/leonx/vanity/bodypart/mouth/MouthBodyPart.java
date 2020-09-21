package top.leonx.vanity.bodypart.mouth;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public class MouthBodyPart extends BodyPart {
    public MouthBodyPart() {

        super(BodyPartProperty.create().setGroup(BodyPartGroup.MOUTH).addFloat("mouth_height", -0.05f, 0.1f, 0f).addFloat("mouth_size", 0.5f, 2, 1f));
    }

    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
