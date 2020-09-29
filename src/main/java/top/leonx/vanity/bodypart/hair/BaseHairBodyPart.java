package top.leonx.vanity.bodypart.hair;

import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.Gender;

public class BaseHairBodyPart extends HairBodyPart {
    public BaseHairBodyPart(Gender suitable) {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.BASE_HAIR_GROUP),suitable);
    }
}
