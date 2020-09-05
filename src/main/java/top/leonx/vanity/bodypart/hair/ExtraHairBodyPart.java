package top.leonx.vanity.bodypart.hair;

import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.bodypart.BodyPartStack;
import top.leonx.vanity.capability.BodyPartCapability;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExtraHairBodyPart extends HairBodyPart {
    public ExtraHairBodyPart() {
        super(BodyPartProperty.create().setGroup(BodyPartGroup.EXTRA_HAIR_GROUP).setMaxStack(3).setPrecondition(
                t->t.getItemStacksList().stream().anyMatch(p->p.getItem().getGroup().equals(BodyPartGroup.BASE_HAIR_GROUP))
        ));
    }

    @Override
    public void adjust(Collection<BodyPartStack> stacks, BodyPartStack selfStack) {
        Optional<BodyPartStack> first = stacks.stream().filter(t -> t.getItem().getGroup().equals(BodyPartGroup.BASE_HAIR_GROUP)).findFirst();
        if(first.isPresent() && selfStack!=null && selfStack.getItem() instanceof ExtraHairBodyPart)
            selfStack.setColor(first.get().getColor());
    }
}
