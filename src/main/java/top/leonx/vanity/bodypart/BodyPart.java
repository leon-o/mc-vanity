package top.leonx.vanity.bodypart;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.util.Gender;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

public abstract class BodyPart implements IHasIcon {
    private final BodyPartProperty   property;
    private String name;
    private ResourceLocation iconLocation;
    public BodyPart(BodyPartProperty property)
    {
        this.property=property;
    }

    public BodyPartProperty getProperty() {
        return property;
    }
    public BodyPartGroup getGroup()
    {
        return property.group;
    }
    public Gender getSuitableGender()
    {
        return Gender.BOTH;
    }
    public List<Integer> getAvailableColors(){
        return property.getAvailableColors();
    }
    public int getRandomColor()
    {
        List<Integer> availableColors = getAvailableColors();
        return availableColors.get((int) (Math.random()*availableColors.size()));
    }

    /**
     * 根据其他的BodyPartStack，调整当前的BodyPartStack
     */
    public void adjustWithContext(Collection<BodyPartStack> stacks, BodyPartStack selfStack)
    {
        //DEFAULT DO NOTHING
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIconLocation() {
        if(iconLocation==null && getName()!=null)
            iconLocation=new ResourceLocation(VanityMod.MOD_ID, String.format("textures/gui/icon/%s.png", getName()));
        return iconLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Nullable
    public ResourceLocation getRegistryName()
    {
        return new ResourceLocation(VanityMod.MOD_ID,getGroup().getName()+"/"+name);
    }
}
