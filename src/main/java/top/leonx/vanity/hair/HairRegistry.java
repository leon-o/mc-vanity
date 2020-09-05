package top.leonx.vanity.hair;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;

public class HairRegistry {
    public enum HairType
    {
        BASE,
        EXTRA
    }
    private final static BiMap<ResourceLocation,IHair> ID_TO_BASE_HAIR = HashBiMap.create();
    private final static BiMap<ResourceLocation,IHair> ID_TO_EXTRA_HAIR = HashBiMap.create();
    public static Set<IHair> getBaseHairs()
    {
        return ID_TO_BASE_HAIR.values();
    }
    public static Set<IHair> getExtraHairs()
    {
        return ID_TO_EXTRA_HAIR.values();
    }
    public static void registry(ResourceLocation location,IHair hair,HairType type)
    {
        if(type.equals(HairType.BASE))
            ID_TO_BASE_HAIR.put(location,hair);
        else
            ID_TO_EXTRA_HAIR.put(location,hair);
    }
    @Nullable
    public static ResourceLocation getHairLocation(IHair hair,HairType type)
    {
        if(type.equals(HairType.BASE))
        {
            return ID_TO_BASE_HAIR.inverse().get(hair);
        }
            return ID_TO_EXTRA_HAIR.inverse().get(hair);
    }
    @Nullable
    public static IHair getHair(ResourceLocation location,HairType type)
    {
        if(type.equals(HairType.BASE))
            return ID_TO_BASE_HAIR.get(location);
        else
            return ID_TO_EXTRA_HAIR.get(location);
    }
}
