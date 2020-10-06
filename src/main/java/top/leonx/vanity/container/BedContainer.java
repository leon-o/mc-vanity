package top.leonx.vanity.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OutsiderHolder;
import top.leonx.vanity.entity.OutsiderIncorporeal;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.network.VanityPacketHandler;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class BedContainer extends Container {
    public PlayerEntity              player;
    public List<OutsiderIncorporeal> entities = new ArrayList<>();
    public BlockPos bedPos;
    VanityBedTileEntity tileEntity;
    public BedContainer(int windowId, PlayerInventory inventory, VanityBedTileEntity tileEntity) {
        super(ModContainerTypes.VANITY_BED_CONTAINER.get(), windowId);
        this.player = inventory.player;
        this.tileEntity = tileEntity;
        if (tileEntity == null) return;
        bedPos = tileEntity.getPos();
        CharacterState characterState = player.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
        OutsiderHolder.getInstance().offlineOutsiders.forEach((key, value) -> {
            if (characterState.getRelationWith(key) < 20) return;
            entities.add(value);
        });
    }
    public BedContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId, inv, (VanityBedTileEntity) inv.player.world.getTileEntity(data.readBlockPos()));
    }

    public static void registerChanel() {
        VanityPacketHandler.registerMessage(OperationMsg.class, OperationMsg::encoder, OperationMsg::decoder, BedContainer::handler);
    }

    private static void handler(OperationMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(() -> {
            ServerPlayerEntity sender = contextSupplier.get().getSender();
            if (sender == null) return;

            OutsiderIncorporeal outsider = OutsiderHolder.getInstance().getOutsider(msg.targetUUID);
            if (outsider == null) return;

            outsider.setHome(msg.bedPos);

            if (msg.bedPos == null) return;
            TileEntity tileEntity = sender.getServerWorld().getTileEntity(msg.bedPos);
            if (!(tileEntity instanceof VanityBedTileEntity)) return;
            VanityBedTileEntity vanityBedTileEntity = (VanityBedTileEntity) tileEntity;
            vanityBedTileEntity.setOwner(msg.targetUUID);


        });
        contextSupplier.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public void requestSetBed(UUID targetID) {
        BlockPos pos = this.tileEntity.getPos();
        VanityPacketHandler.CHANNEL.sendToServer(new OperationMsg(targetID, pos));
    }
    @OnlyIn(Dist.CLIENT)
    public void requestUnsetBed(UUID targetID) {
        VanityPacketHandler.CHANNEL.sendToServer(new OperationMsg(targetID, null));
    }
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public static class OperationMsg {
        public UUID targetUUID;
        public BlockPos bedPos;
        boolean trueSetFalseUnset = true;

        public OperationMsg(UUID targetUUID, BlockPos bedPos) {
            this.targetUUID = targetUUID;
            trueSetFalseUnset = bedPos != null;
            this.bedPos = bedPos;
        }

        public static OperationMsg decoder(PacketBuffer buffer) {
            UUID     uuid        = buffer.readUniqueId();
            boolean  setOrCancel = buffer.readBoolean();
            BlockPos pos         = null;
            if (setOrCancel) pos = buffer.readBlockPos();
            return new OperationMsg(uuid, pos);
        }

        public static void encoder(OperationMsg msg, PacketBuffer buffer) {
            buffer.writeUniqueId(msg.targetUUID);
            buffer.writeBoolean(msg.trueSetFalseUnset);
            if (msg.trueSetFalseUnset) buffer.writeBlockPos(msg.bedPos);
        }
    }
}
