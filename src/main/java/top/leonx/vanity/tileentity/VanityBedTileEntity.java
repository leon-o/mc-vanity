package top.leonx.vanity.tileentity;

import net.minecraft.block.BedBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.container.BedContainer;
import top.leonx.vanity.container.VanityMirrorContainer;
import top.leonx.vanity.init.ModTileEntityTypes;

import javax.annotation.Nullable;
import java.util.UUID;

public class VanityBedTileEntity extends TileEntity implements INamedContainerProvider {
    private DyeColor color;
    private UUID owner=new UUID(0,0);
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

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putUniqueId("owner",owner);
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        compound.getUniqueId("owner");
        super.read(compound);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }
    //Call when invoke world::notifyBlockChange

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(pkt.getNbtCompound());
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

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Vanity Mirror");
    }

    @Nullable
    @Override
    public Container createMenu(int window_id, PlayerInventory inventory, PlayerEntity player) {
        return new BedContainer(window_id, inventory, this);
    }
}
