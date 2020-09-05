package top.leonx.vanity.bodypart;

import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.hair.IHasIcon;
import top.leonx.vanity.init.ModBodyParts;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class BodyPartGroup {
    public static final Map<String, BodyPartGroup> GROUPS = new HashMap<>();

    public static final BodyPartGroup BASE_HAIR_GROUP  = new BodyPartGroup(BodyPartCategory.HAIR, "base_hair", () -> ModBodyParts.FRINGE_1, 1, t -> 0.3);
    public static final BodyPartGroup EXTRA_HAIR_GROUP = new BodyPartGroup(BodyPartCategory.HAIR, "extra_hair", () -> ModBodyParts.LONG_DOUBLE_PONYTAIL, 2);
    public static final BodyPartGroup EYE_GROUP        = new BodyPartGroup(BodyPartCategory.HAIR, "eye", () -> ModBodyParts.EYE_1, 1);
    public static final BodyPartGroup SKIN_GROUP       = new BodyPartGroup(BodyPartCategory.HAIR, "skin", () -> ModBodyParts.SKIN_FEMALE_1, 1);
    public static final BodyPartGroup MOUTH            = new BodyPartGroup(BodyPartCategory.HAIR, "mouth", () -> ModBodyParts.MOUTH_DEBUG, 1);

    private final String                                            name;
    private final Supplier<IHasIcon>                                icon;
    private final int                                               maxStack;
    private final Function<BodyPartCapability.BodyPartData, Double> emptyRate;
    private final BodyPartCategory                                  parent;

    public BodyPartGroup(BodyPartCategory category, String name, Supplier<IHasIcon> icon, int maxStack, Function<BodyPartCapability.BodyPartData, Double> emptyRate) {
        this.name = name;
        this.icon = icon;
        category.addGroup(this);
        parent = category;
        this.maxStack = maxStack;
        this.emptyRate = emptyRate;
        GROUPS.put(name, this);
    }

    public BodyPartGroup(BodyPartCategory category, String name, Supplier<IHasIcon> icon, int maxStack) {
        this(category, name, icon, maxStack, t -> 0D);
    }

    @Nullable
    public static BodyPartGroup getGroupByName(String name) {
        return GROUPS.get(name);
    }

    public double getEmptyRate(BodyPartCapability.BodyPartData data) {
        return emptyRate.apply(data);
    }

    public BodyPartCategory getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public IHasIcon getIcon() {
        return icon.get();
    }
}
