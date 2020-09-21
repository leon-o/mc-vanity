package top.leonx.vanity.bodypart;

import com.google.common.collect.Lists;

import java.util.List;

public class DressBodyPart extends BodyPart {
    public DressBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EXTRA_HAIR_GROUP));
    }

    @Override
    public List<Integer> getAvailableColors() {
        return Lists.newArrayList(0xFFFFFF);
    }
}
