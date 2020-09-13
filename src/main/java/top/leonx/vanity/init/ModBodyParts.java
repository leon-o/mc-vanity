package top.leonx.vanity.init;

import top.leonx.vanity.bodypart.AbstractBodyPart;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.bodypart.eye.EyeBodyPart;
import top.leonx.vanity.bodypart.hair.BaseHairBodyPart;
import top.leonx.vanity.bodypart.hair.ExtraHairBodyPart;
import top.leonx.vanity.bodypart.mouth.MouthBodyPart;
import top.leonx.vanity.bodypart.skin.SkinBodyPart;

public class ModBodyParts {
    public static final AbstractBodyPart FRINGE_1             = new BaseHairBodyPart().setRegistryName("fringe_1");
    public static final AbstractBodyPart FRINGE_2             = new BaseHairBodyPart().setRegistryName("fringe_2");
    public static final AbstractBodyPart PONYTAIL             = new ExtraHairBodyPart().setRegistryName("ponytail");
    public static final AbstractBodyPart LONG_HAIR            = new ExtraHairBodyPart().setRegistryName("long_hair");
    public static final AbstractBodyPart DOUBLE_PONYTAIL      = new ExtraHairBodyPart().setRegistryName("double_ponytail");
    public static final AbstractBodyPart LONG_DOUBLE_PONYTAIL = new ExtraHairBodyPart().setRegistryName("long_double_ponytail");
    public static final AbstractBodyPart MOHICAN              = new ExtraHairBodyPart().setRegistryName("mohican");
    public static final AbstractBodyPart FRINGE_3             = new BaseHairBodyPart().setRegistryName("fringe_3");
    public static final AbstractBodyPart FRINGE_4             = new BaseHairBodyPart().setRegistryName("fringe_4");

//    public static final AbstractBodyPart EYE_DEBUG = new EyeBodyPart().setRegistryName("eye_debug");
    public static final AbstractBodyPart EYE_1     = new EyeBodyPart().setRegistryName("eye_1");
    public static final AbstractBodyPart EYE_2     = new EyeBodyPart().setRegistryName("eye_2");
    public static final AbstractBodyPart EYE_3     = new EyeBodyPart().setRegistryName("eye_3");

    public static final AbstractBodyPart   SKIN_FEMALE_1 = new SkinBodyPart().setRegistryName("skin_female_1");

//    public static final AbstractBodyPart   MOUTH_DEBUG = new MouthBodyPart().setRegistryName("mouth_debug");
    public static final AbstractBodyPart   MOUTH_1=new MouthBodyPart().setRegistryName("mouth_1");


    public static final AbstractBodyPart[] ITEMS         = {
            FRINGE_1, FRINGE_2, PONYTAIL, LONG_HAIR, DOUBLE_PONYTAIL, LONG_DOUBLE_PONYTAIL, MOHICAN,FRINGE_3, FRINGE_4,
            EYE_1,EYE_2,EYE_3, SKIN_FEMALE_1,
            /*MOUTH_DEBUG,*/MOUTH_1
    };

    public static void register() {
        for (AbstractBodyPart hair : ITEMS) {
            BodyPartRegistry.registry(hair);
        }
    }
}
