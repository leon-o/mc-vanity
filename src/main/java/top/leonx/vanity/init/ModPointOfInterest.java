package top.leonx.vanity.init;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.village.PointOfInterestType;

import java.util.Set;

public class ModPointOfInterest {
    private static Set<BlockState> getAllStates(Block blockIn) {
        return ImmutableSet.copyOf(blockIn.getStateContainer().getValidStates());
    }

    public static final PointOfInterestType CRAFT_TABLE=new PointOfInterestType("craft_table", getAllStates(Blocks.CRAFTING_TABLE),1,1);

    public static final PointOfInterestType[] ALL_INTEREST_TYPE={
            CRAFT_TABLE.setRegistryName("craft_table"),
    };
}
