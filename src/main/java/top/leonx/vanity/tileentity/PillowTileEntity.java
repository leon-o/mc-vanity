package top.leonx.vanity.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.init.ModTileEntityTypes;
import top.leonx.vanity.wordsavedata.BedWorldSaveData;

import java.util.UUID;

public class PillowTileEntity extends TileEntity {
    public UUID ownerId;
    public PillowTileEntity() {
        super(ModTileEntityTypes.PILLOW_TILE_ENTITY);
    }

    public void readFromItemStack(ItemStack stack) {
        CompoundNBT owner = stack.getChildTag("owner");
        if(owner!=null) {
            ownerId = owner.getUniqueId("uuid");
            writeToWorldSaveData();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //writeToWorldSaveData();
    }

    @Override
    public void remove() {
        super.remove();
        removeFromWorldSaveData();
    }

    private void writeToWorldSaveData()
    {
        if(world==null || world.isRemote())return;
        ServerWorld serverWorld = (ServerWorld) this.world;
        BedWorldSaveData.get(serverWorld).addBedPos(ownerId,this.pos);
    }

    private void removeFromWorldSaveData()
    {
        if(world==null || world.isRemote())return;
        ServerWorld serverWorld = (ServerWorld) this.world;
        BedWorldSaveData.get(serverWorld).removeBedPos(ownerId,this.pos);
    }
}
