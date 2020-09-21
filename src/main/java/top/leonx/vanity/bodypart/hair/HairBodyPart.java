package top.leonx.vanity.bodypart.hair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartProperty;
import top.leonx.vanity.util.ColorUtil;

import java.util.List;

public abstract class HairBodyPart extends BodyPart {
    ResourceLocation iconLocation;
    public HairBodyPart(BodyPartProperty property) {
        super(property);
    }


    @Override
    public List<Integer> getAvailableColors() {
        return ColorUtil.COLORS;
    }
}
