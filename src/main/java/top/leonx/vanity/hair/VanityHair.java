package top.leonx.vanity.hair;

import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

public abstract class VanityHair implements IHair,IHasIcon{
    private String registryName;
    private HairRegistry.HairType type;
    private ResourceLocation iconLocation;
    public VanityHair(String registryName, HairRegistry.HairType type){
        this.registryName=registryName;
        this.type=type;
        this.iconLocation=new ResourceLocation(VanityMod.MOD_ID, "textures/item/"+registryName+".png");
    }

    public String getRegistryName() {
        return registryName;
    }

    public HairRegistry.HairType getType() {
        return type;
    }

    @Override
    public ResourceLocation getIconLocation() {
        return iconLocation;
    }
}
