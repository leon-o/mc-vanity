package top.leonx.vanity.init;

import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.bodypart.BodyPartRegistryEntry;
import top.leonx.vanity.bodypart.DressBodyPart;
import top.leonx.vanity.bodypart.eye.EyeBodyPart;
import top.leonx.vanity.bodypart.hair.BaseHairBodyPart;
import top.leonx.vanity.bodypart.mouth.MouthBodyPart;
import top.leonx.vanity.bodypart.skin.SkinBodyPart;
import top.leonx.vanity.util.Gender;

public class ModBodyParts {
    public static final BodyPartRegistryEntry FRINGE_1             = BodyPartRegistry.registry("fringe_1", ()->new BaseHairBodyPart(Gender.BOTH));
    public static final BodyPartRegistryEntry FRINGE_2             = BodyPartRegistry.registry("fringe_2", ()->new BaseHairBodyPart(Gender.BOTH));
    public static final BodyPartRegistryEntry PONYTAIL             = BodyPartRegistry.registry("ponytail", ()->new BaseHairBodyPart(Gender.FEMALE));
    public static final BodyPartRegistryEntry LONG_HAIR            = BodyPartRegistry.registry("long_hair", ()->new BaseHairBodyPart(Gender.FEMALE));
    public static final BodyPartRegistryEntry DOUBLE_PONYTAIL      = BodyPartRegistry.registry("double_ponytail", ()->new BaseHairBodyPart(Gender.FEMALE));
    public static final BodyPartRegistryEntry LONG_DOUBLE_PONYTAIL = BodyPartRegistry.registry("long_double_ponytail", ()->new BaseHairBodyPart(Gender.FEMALE));
    public static final BodyPartRegistryEntry MOHICAN              = BodyPartRegistry.registry("mohican", ()->new BaseHairBodyPart(Gender.MALE));
    public static final BodyPartRegistryEntry FRINGE_3             = BodyPartRegistry.registry("fringe_3", ()->new BaseHairBodyPart(Gender.BOTH));
    public static final BodyPartRegistryEntry FRINGE_4             = BodyPartRegistry.registry("fringe_4", ()->new BaseHairBodyPart(Gender.BOTH));

    //    public static final AbstractBodyPart EYE_DEBUG = new EyeBodyPart().setRegistryName("eye_debug");
    public static final BodyPartRegistryEntry EYE_1 = BodyPartRegistry.registry("eye_1",EyeBodyPart::new);
    public static final BodyPartRegistryEntry EYE_2 = BodyPartRegistry.registry("eye_2",EyeBodyPart::new);
    public static final BodyPartRegistryEntry EYE_3 = BodyPartRegistry.registry("eye_3",EyeBodyPart::new);

    public static final BodyPartRegistryEntry SKIN_FEMALE_1 = BodyPartRegistry.registry("skin_female_1",()->new SkinBodyPart(Gender.FEMALE));
    public static final BodyPartRegistryEntry SKIN_MALE_1 = BodyPartRegistry.registry("skin_male_1",()->new SkinBodyPart(Gender.MALE));

    //    public static final AbstractBodyPart   MOUTH_DEBUG = new MouthBodyPart().setRegistryName("mouth_debug");
    public static final BodyPartRegistryEntry MOUTH_1 = BodyPartRegistry.registry("mouth_1",MouthBodyPart::new);

    public static final BodyPartRegistryEntry DRESS_DEBUG = BodyPartRegistry.registry("dress_debug",DressBodyPart::new);

/*    public static final BodyPart[] ITEMS = {FRINGE_1, FRINGE_2, PONYTAIL, LONG_HAIR, DOUBLE_PONYTAIL, LONG_DOUBLE_PONYTAIL, MOHICAN, FRINGE_3, FRINGE_4, EYE_1, EYE_2, EYE_3, SKIN_FEMALE_1,
            *//*MOUTH_DEBUG,*//*MOUTH_1, DRESS_DEBUG};*/

/*    public static void register() {
        for (BodyPart hair : ITEMS) {
            BodyPartRegistry.registry(hair);
        }
    }*/
}
