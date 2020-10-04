package top.leonx.vanity.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OfflineOutsider;
import top.leonx.vanity.entity.OutsiderHolder;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

import java.util.ArrayList;
import java.util.List;

public class BedContainer extends Container {
    public PlayerEntity player;
    public List<OfflineOutsider> entities = new ArrayList<>();
    VanityBedTileEntity tileEntity;

    public BedContainer(int windowId, PlayerInventory inventory, VanityBedTileEntity tileEntity) {
        super(ModContainerTypes.VANITY_BED_CONTAINER.get(), windowId);
        this.player = inventory.player;
        this.tileEntity = tileEntity;
        CharacterState characterState = player.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
        OutsiderHolder.offlineOutsiders.forEach((key, value) -> {
            if (characterState.getRelationWith(key) < 20) return;
            entities.add(value);
        });
    }

    public BedContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId, inv, (VanityBedTileEntity) inv.player.world.getTileEntity(data.readBlockPos()));
    }


    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
