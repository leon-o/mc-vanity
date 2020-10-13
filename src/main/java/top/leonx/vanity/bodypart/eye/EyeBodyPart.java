package top.leonx.vanity.bodypart.eye;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;

public class EyeBodyPart extends BodyPart {
    public EyeBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EYE_GROUP).addFloat("eye_height",-0.175f,0.175f,0));
    }
}
