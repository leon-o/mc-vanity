package top.leonx.vanity.hair;

import net.minecraft.client.renderer.RenderType;
import top.leonx.vanity.client.models.AbstractHairModel;

public interface IHair {
    AbstractHairModel getHairModel();
    RenderType getRenderType();
}
