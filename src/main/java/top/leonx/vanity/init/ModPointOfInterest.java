package top.leonx.vanity.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import top.leonx.vanity.VanityMod;

import java.util.Set;

public class ModPointOfInterest {
    public static final DeferredRegister<PointOfInterestType> POI_TYPE = new DeferredRegister<>(
            ForgeRegistries.POI_TYPES, VanityMod.MOD_ID);
    public static final RegistryObject<PointOfInterestType> CRAFT_TABLE = POI_TYPE.register("craft_table",
                                                                                            () -> new PointOfInterestType(
                                                                                                    "craft_table",
                                                                                                    getAllStates(
                                                                                                            Blocks.CRAFTING_TABLE),
                                                                                                    1, 1));
    public static final RegistryObject<PointOfInterestType> FURNACE     = POI_TYPE.register("furnace",
                                                                                            () -> new PointOfInterestType(
                                                                                                    "furnace",
                                                                                                    getAllStates(
                                                                                                            Blocks.FURNACE),
                                                                                                    1, 1));

    public static final RegistryObject<PointOfInterestType> BLAST_FURNACE = POI_TYPE.register("blast_furnace",
                                                                                              () -> new PointOfInterestType(
                                                                                                    "blast_furnace",
                                                                                                    getAllStates(
                                                                                                            Blocks.BLAST_FURNACE),
                                                                                                    1, 1));
    public static final RegistryObject<PointOfInterestType> SMOKER = POI_TYPE.register("smoker",
                                                                                              () -> new PointOfInterestType(
                                                                                                      "blast_furnace",
                                                                                                      getAllStates(
                                                                                                              Blocks.SMOKER),
                                                                                                      1, 1));
    private static Set<BlockState> getAllStates(Block blockIn) {
        return ImmutableSet.copyOf(blockIn.getStateContainer().getValidStates());
    }
}
