package top.leonx.vanity.bodypart;

import top.leonx.vanity.hair.IHasIcon;
import top.leonx.vanity.init.ModBodyParts;

import java.util.ArrayList;
import java.util.List;

public class BodyPartCategory {
    public static final BodyPartCategory HAIR =new BodyPartCategory("hair", ModBodyParts.FRINGE_1);

    private final           String          name;
    private final           IHasIcon icon;
    public List<BodyPartGroup>       children =new ArrayList<>();
    public BodyPartCategory(String name, IHasIcon icon)
    {
        this.name=name;
        this.icon=icon;
    }
    public void addGroup(BodyPartGroup group)
    {
        children.add(group);
    }
    public String getName() {
        return name;
    }

    public IHasIcon getIcon() {
        return icon;
    }
}
