package top.leonx.vanity.bodypart.hair;

import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.bodypart.BodyPartStack;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.util.Gender;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtraHairBodyPart extends HairBodyPart {
    public ExtraHairBodyPart(Gender suitable) {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EXTRA_HAIR_GROUP).setMaxStack(3).setPrecondition(
                t->t.getItemStacksList().stream().anyMatch(p->p.getItem().getGroup().equals(BodyPartGroup.BASE_HAIR_GROUP))
        ),suitable);
    }

    @Override
    public void adjustWithContext(Collection<BodyPartStack> stacks, BodyPartStack selfStack) {
        //获取已有的base hair，并将颜色设置成与base hair相同的颜色
        Optional<BodyPartStack> firstBaseHair = stacks.stream().filter(t -> t.getItem().getGroup().equals(BodyPartGroup.BASE_HAIR_GROUP)).findFirst();
        if(firstBaseHair.isPresent() && selfStack!=null && selfStack.getItem() instanceof ExtraHairBodyPart)
            selfStack.setColor(firstBaseHair.get().getColor());
    }
}
