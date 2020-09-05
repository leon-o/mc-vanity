package top.leonx.vanity.client;

import top.leonx.vanity.bodypart.AbstractBodyPart;

import java.util.HashMap;
import java.util.Map;

public class BodyPartRendererRegistry {
    public static final Map<AbstractBodyPart, BodyPartRenderer> ITEM_TO_RENDERER =new HashMap<>();
    public static void register(AbstractBodyPart item, BodyPartRenderer renderer)
    {
        ITEM_TO_RENDERER.put(item,renderer);
    }
    public static BodyPartRenderer getRenderer(AbstractBodyPart item)
    {
        return ITEM_TO_RENDERER.get(item);
    }
}
