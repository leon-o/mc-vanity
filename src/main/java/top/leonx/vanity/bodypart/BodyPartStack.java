package top.leonx.vanity.bodypart;

import javafx.util.Pair;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import org.antlr.v4.runtime.misc.Triple;

import java.util.HashMap;
import java.util.Map;

public class BodyPartStack {
    AbstractBodyPart item;
    int              color;
    Map<String,Float> adjustableAttributes=new HashMap<>();
    public BodyPartStack(AbstractBodyPart item)
    {
        this(item,0xFFFFFF);
    }
    public BodyPartStack(AbstractBodyPart item, int color)
    {
        this.item=item;
        this.color=color;
        adjustableAttributes=new HashMap<>();
        for (BodyPartProperty.AdjustableAttribute entry : item.getProperty().adjustableAttributes) {
            adjustableAttributes.put(entry.getName(),entry.defaultV);
        }

    }

    public BodyPartStack(AbstractBodyPart item, int color, Map<String,Float> attributes) {
        this.item=item;
        this.color=color;
        adjustableAttributes=attributes;
    }

    public AbstractBodyPart getItem() {
        return item;
    }

    public void setItem(AbstractBodyPart item) {
        this.item = item;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Map<String, Float> getAdjustableAttributes() {
        return adjustableAttributes;
    }

    public CompoundNBT createNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        ResourceLocation location=BodyPartRegistry.getLocation(item);
        if(location==null)
            return null;
        nbt.putString("group",item.getGroup().getName());
        nbt.putString("namespace", location.getNamespace());
        nbt.putString("path", location.getPath());
        nbt.putInt("color",getColor());
        ListNBT attributes = new ListNBT();
        for (Map.Entry<String, Float> entry : adjustableAttributes.entrySet()) {
            CompoundNBT attributeNBT=new CompoundNBT();
            attributeNBT.putString("key",entry.getKey());
            attributeNBT.putFloat("value",entry.getValue());
            attributes.add(attributeNBT);
        }
        nbt.put("attribute",attributes);

        return nbt;
    }
    public static BodyPartStack createFromNBT(CompoundNBT nbt)
    {
        BodyPartGroup group       = BodyPartGroup.getGroupByName(nbt.getString("group"));
        if(group==null) return null;
        ResourceLocation location=new ResourceLocation(nbt.getString("namespace"),nbt.getString("path"));
        AbstractBodyPart item  = BodyPartRegistry.getBodyPart(group, location);
        if(item==null) return null;
        int              color =nbt.getInt("color");
        ListNBT attributes=nbt.getList("attribute",10);
        Map<String,Float> attributeMap=new HashMap<>();
        for (INBT attribute : attributes) {
            if(attribute.getId()!=10) continue;
            CompoundNBT atrComponent=(CompoundNBT)attribute;
            attributeMap.put(atrComponent.getString("key"),atrComponent.getFloat("value"));
        }
        return new BodyPartStack(item,color,attributeMap);
    }
}
