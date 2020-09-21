package top.leonx.vanity.client;

import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartRegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class BodyPartRendererRegistry {
    public static final Map<BodyPart, BodyPartRenderer> ITEM_TO_RENDERER =new HashMap<>();
    public static void register(BodyPartRegistryEntry item, BodyPartRenderer renderer)
    {
        ITEM_TO_RENDERER.put(item.get(),renderer);
    }
    public static BodyPartRenderer getRenderer(BodyPart item)
    {
        return ITEM_TO_RENDERER.get(item);
    }
}
