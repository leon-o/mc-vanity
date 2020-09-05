package top.leonx.vanity.bodypart;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.hair.IHasIcon;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractBodyPart implements IHasIcon {
    private       String           registryName;
    private final BodyPartProperty property;
    private       int              color;
    public AbstractBodyPart(BodyPartProperty property)
    {
        this.property=property;
    }

    public AbstractBodyPart setRegistryName(String registryName) {
        this.registryName = registryName;
        return this;
    }

    public String getRegistryName() {
        return registryName;
    }

    public BodyPartProperty getProperty() {
        return property;
    }
    public BodyPartGroup getGroup()
    {
        return property.group;
    }
    public abstract List<Integer> getAvailableColors();
    public int getRandomColor()
    {
        List<Integer> availableColors = getAvailableColors();
        return availableColors.get((int) (Math.random()*availableColors.size()));
    }

    /**
     * 调整
     * @param stacks
     * @param selfStack
     */
    public void adjust(Collection<BodyPartStack> stacks, BodyPartStack selfStack)
    {
        // DO NOTHING
    }
    ResourceLocation iconLocation;
    @Override
    public ResourceLocation getIconLocation() {
        if(iconLocation==null&&getRegistryName()!=null)
            iconLocation=new ResourceLocation(VanityMod.MOD_ID, String.format("textures/gui/icon/%s.png", getRegistryName()));
        return iconLocation;
    }
}
