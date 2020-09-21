package top.leonx.vanity.bodypart;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class BodyPartRegistry {

    private final static Map<BodyPartGroup, BiMap<ResourceLocation, BodyPart>> ID_TO_VANITY_ITEM = new HashMap<>();
    private final static HashSet<BodyPartRegistryEntry> bodyPartRegistrySupplier=new HashSet<>();
    static {
        for (BodyPartGroup group : BodyPartGroup.GROUPS.values()) {
            ID_TO_VANITY_ITEM.put(group, HashBiMap.create());
        }
    }
    public static BodyPartRegistryEntry registry(String name,Supplier<BodyPart> bodyPartSupplier)
    {
        BodyPartRegistryEntry entry = new BodyPartRegistryEntry(bodyPartSupplier, name);
        bodyPartRegistrySupplier.add(entry);

        return entry;
    }
    public static void initBodyParts(final FMLCommonSetupEvent event)
    {
        for (BodyPartRegistryEntry registryEntry : bodyPartRegistrySupplier) {
            BodyPart bodyPart = registryEntry.bodyPartCreater.get();
            bodyPart.setName(registryEntry.name);
            registryEntry.updateReference(bodyPart);

            registry(bodyPart);
        }
    }
    private static void registry(BodyPart item) {
        if (item.getGroup() == null || item.getRegistryName()==null) return;
        ResourceLocation                  location = item.getRegistryName();
        BiMap<ResourceLocation, BodyPart> map      = ID_TO_VANITY_ITEM.computeIfAbsent(item.getGroup(), k -> HashBiMap.create());

        map.put(location, item);
    }

    @Nullable
    public static ResourceLocation getLocation(BodyPart item)
    {
        BiMap<ResourceLocation, BodyPart> map = ID_TO_VANITY_ITEM.get(item.getGroup());
        if(map!=null)
        {
            return map.inverse().get(item);
        }
        return null;
    }
    @Nullable
    public static BodyPart getBodyPart(BodyPartGroup group, ResourceLocation location)
    {
        BiMap<ResourceLocation, BodyPart> map = ID_TO_VANITY_ITEM.get(group);
        if(map!=null)
        {
            return map.get(location);
        }
        return null;
    }
    public static Set<BodyPart> getBodyParts(BodyPartGroup group)
    {
        return ID_TO_VANITY_ITEM.get(group).values();
    }
}
