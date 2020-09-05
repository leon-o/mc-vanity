package top.leonx.vanity.bodypart;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BodyPartRegistry {

    private final static Map<BodyPartGroup, BiMap<ResourceLocation, AbstractBodyPart>> ID_TO_VANITY_ITEM = new HashMap<>();

    static {
        for (BodyPartGroup group : BodyPartGroup.GROUPS.values()) {
            ID_TO_VANITY_ITEM.put(group, HashBiMap.create());
        }
    }

    public static void registry(AbstractBodyPart item) {
        if (item.getGroup() == null) return;
        ResourceLocation                          location = new ResourceLocation(VanityMod.MOD_ID, item.getGroup().getName() + "_" + item.getRegistryName());
        BiMap<ResourceLocation, AbstractBodyPart> map      = ID_TO_VANITY_ITEM.computeIfAbsent(item.getGroup(), k -> HashBiMap.create());

        map.put(location, item);
    }

    @Nullable
    public static ResourceLocation getLocation(AbstractBodyPart item)
    {
        BiMap<ResourceLocation, AbstractBodyPart> map = ID_TO_VANITY_ITEM.get(item.getGroup());
        if(map!=null)
        {
            return map.inverse().get(item);
        }
        return null;
    }
    @Nullable
    public static AbstractBodyPart getBodyPart(BodyPartGroup group, ResourceLocation location)
    {
        BiMap<ResourceLocation, AbstractBodyPart> map = ID_TO_VANITY_ITEM.get(group);
        if(map!=null)
        {
            return map.get(location);
        }
        return null;
    }
    public static Set<AbstractBodyPart> getBodyParts(BodyPartGroup group)
    {
        return ID_TO_VANITY_ITEM.get(group).values();
    }
}
