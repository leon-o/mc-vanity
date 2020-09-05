package top.leonx.vanity.bodypart.hair;

import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;

public class BaseHairBodyPart extends HairBodyPart {
    public BaseHairBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.BASE_HAIR_GROUP));
    }
}
