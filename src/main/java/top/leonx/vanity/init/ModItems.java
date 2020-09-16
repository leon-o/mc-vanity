package top.leonx.vanity.init;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import top.leonx.vanity.item.PillowItem;

@SuppressWarnings("ConstantConditions")
public class ModItems {
//    public static final Item PONYTAIL=new PonyTailItem(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("ponytail");
//    public static final Item DOUBLE_PONYTAIL=new DoublePonyTailItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item MOHICAN=new MohicanHairItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item LONG_HAIR=new LongHairItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item LONG_DOUBLE_PONYTAIL=new LongDoublePonyTailItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item FRINGE_1=new FringeHair(new Item.Properties().group(ItemGroup.MISC),"fringe_1").setRegistryName("fringe_1");
//    public static final Item FRINGE_2=new FringeHair(new Item.Properties().group(ItemGroup.MISC),"fringe_2").setRegistryName("fringe_2");
    public static final Item VANITY_MIRROR=
            new BlockItem(ModBlocks.VANITY_MIRROR, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(ModBlocks.VANITY_MIRROR.getRegistryName());
    public static final Item PILLOW_BLOCk=new PillowItem().setRegistryName(ModBlocks.PILLOW.getRegistryName());

    public static final Item[] ITEMS={
//            PONYTAIL,
//            DOUBLE_PONYTAIL,
//            MOHICAN,
//            LONG_HAIR,
//            LONG_DOUBLE_PONYTAIL,
//            FRINGE_1,
//            FRINGE_2,
            VANITY_MIRROR,
            PILLOW_BLOCk
    };
}
