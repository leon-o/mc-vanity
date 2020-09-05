package top.leonx.vanity.bodypart;

import javafx.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import org.antlr.v4.runtime.misc.Triple;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.BodyPartCapability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BodyPartProperty {
    public BodyPartGroup group;
    public Function<BodyPartCapability.BodyPartData,Boolean> precondition=t->true;
    public int maxStack=1;
    private float rarity=1;
    private float                     commonness           =1;
    public  List<AdjustableAttribute> adjustableAttributes =new ArrayList<>();
    public static BodyPartProperty create()
    {
        return new BodyPartProperty();
    }

    public BodyPartProperty setGroup(BodyPartGroup group) {
        this.group = group;
        return this;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public BodyPartProperty setPrecondition(Function<BodyPartCapability.BodyPartData,Boolean> precondition)
    {
        this.precondition=precondition;
        return this;
    }
    public BodyPartProperty setRarity(float rarity)
    {
        this.rarity=rarity;
        this.commonness=1/rarity;
        return this;
    }

    public float getRarity() {
        return rarity;
    }

    public float getCommonness() {
        return commonness;
    }

    public BodyPartProperty setMaxStack(int maxStack) {
        this.maxStack = maxStack;
        return this;
    }

    public BodyPartProperty addFloat(String key,float min,float max,float defaultV)
    {
        adjustableAttributes.add(new AdjustableAttribute(key,min,max,defaultV));
        return this;
    }


    public static class AdjustableAttribute {
        float min,max,defaultV;
        String name;

        public AdjustableAttribute(String name, float min, float max, float defaultV) {
            this.min = min;
            this.max = max;
            this.defaultV = defaultV;
            this.name = name;
        }

        public float getMin() {
            return min;
        }

        public float getMax() {
            return max;
        }

        public float getDefaultV() {
            return defaultV;
        }

        public String getName() {
            return name;
        }

        public String getTranslateKey()
        {
            return "attr."+ VanityMod.MOD_ID+"."+name;
        }
    }
}
