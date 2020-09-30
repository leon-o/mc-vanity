package top.leonx.vanity.tileentity;

import net.minecraft.block.BedBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.init.ModTileEntityTypes;

public class VanityBedTileEntity extends TileEntity {
    private DyeColor color;

    public VanityBedTileEntity() {
        super(ModTileEntityTypes.VANITY_BED.get());
    }

    public VanityBedTileEntity(DyeColor colorIn) {
        this();
        this.setColor(colorIn);
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 11, this.getUpdateTag());
    }

    @OnlyIn(Dist.CLIENT)
    public DyeColor getColor() {
        if (this.color == null) {
            this.color = ((BedBlock)this.getBlockState().getBlock()).getColor();
        }

        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }
}
