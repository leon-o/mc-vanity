package top.leonx.vanity.bodypart;

import com.google.common.collect.ImmutableList;
import top.leonx.vanity.hair.IHasIcon;
import top.leonx.vanity.init.ModBodyParts;

import java.util.List;

public enum BodyPartCategories {
    BASE_HAIR(ModBodyParts.FRINGE_1.get()),
    EXTRA_HAIR(ModBodyParts.DOUBLE_PONYTAIL.get());

    private final ImmutableList<IHasIcon> icons;

    BodyPartCategories(IHasIcon... icons) {
        this.icons = ImmutableList.copyOf(icons);
    }
    public List<IHasIcon> getIcons() {
        return this.icons;
    }
}
