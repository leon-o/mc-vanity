package top.leonx.vanity.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;

import java.util.Set;

public class ModPointOfInterest {
    public static final DeferredRegister<PointOfInterestType> POI_TYPE = new DeferredRegister<>(ForgeRegistries.POI_TYPES, VanityMod.MOD_ID);


    private static Set<BlockState> getAllStates(Block blockIn) {
        return ImmutableSet.copyOf(blockIn.getStateContainer().getValidStates());
    }

    public static final RegistryObject<PointOfInterestType> CRAFT_TABLE=POI_TYPE.register("craft_table",()->new PointOfInterestType("craft_table",getAllStates(Blocks.CRAFTING_TABLE),1,1)) ;

}
