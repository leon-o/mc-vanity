package top.leonx.vanity.init;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.item.PillowItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, VanityMod.MOD_ID);

    //    public static final Item PONYTAIL=new PonyTailItem(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("ponytail");
//    public static final Item DOUBLE_PONYTAIL=new DoublePonyTailItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item MOHICAN=new MohicanHairItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item LONG_HAIR=new LongHairItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item LONG_DOUBLE_PONYTAIL=new LongDoublePonyTailItem(new Item.Properties().group(ItemGroup.MISC));
//    public static final Item FRINGE_1=new FringeHair(new Item.Properties().group(ItemGroup.MISC),"fringe_1").setRegistryName("fringe_1");
//    public static final Item FRINGE_2=new FringeHair(new Item.Properties().group(ItemGroup.MISC),"fringe_2").setRegistryName("fringe_2");

    public static final RegistryObject<Item> VANITY_MIRROR=ITEMS.register("vanity_mirror",
                                                          ()->createBlockItem(ModBlocks.VANITY_MIRROR,new Item.Properties().group(ItemGroup.DECORATIONS)));

    public static final RegistryObject<Item> PILLOW_BLOCk=ITEMS.register("pillow",PillowItem::new);

    public static BlockItem createBlockItem(RegistryObject<Block> block, Item.Properties properties)
    {
        return new BlockItem(block.get(),properties);
    }
}
