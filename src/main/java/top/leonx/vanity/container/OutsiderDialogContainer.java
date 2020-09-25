package top.leonx.vanity.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.entity.OutsiderRequest;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.network.VanityPacketHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class OutsiderDialogContainer extends Container {
    static {
        VanityPacketHandler.registerMessage(3, OperationMsg.class, OperationMsg::encoder, OperationMsg::decoder, OutsiderDialogContainer::handler);
    }

    public OutsiderEntity outsider;
    PlayerEntity player;
    public PlayerEntity getPlayer() {
        return player;
    }
    public List<OutsiderRequest> availableRequests;
    public OutsiderDialogContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId,inv,readOutsiderFromBuffer(data));
    }

    public OutsiderDialogContainer(int windowId, PlayerInventory inv, OutsiderEntity entity) {
        super(ModContainerTypes.OUTSIDER_DIALOG.get(), windowId);
        this.outsider = entity;
        player = inv.player;
        updateAvailableRequest();
    }
    private static OutsiderEntity readOutsiderFromBuffer(PacketBuffer data)
    {
        if (data != null && Minecraft.getInstance().world != null) {
            Entity entityByID = Minecraft.getInstance().world.getEntityByID(data.readInt());
            if (entityByID instanceof OutsiderEntity) return  (OutsiderEntity) entityByID;
        }

        return null;
    }

    public void updateAvailableRequest()
    {
        availableRequests =OutsiderRequest.getOperations().stream().filter(t->t.canExecute(player, outsider)).collect(Collectors.toList());
    }
    private static void handler(OperationMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            if (sender == null) return;
            if (sender.openContainer instanceof OutsiderDialogContainer) {
                OutsiderDialogContainer container = (OutsiderDialogContainer) sender.openContainer;
                OutsiderRequest.getRequest(msg.name).Execute(container.player, container.outsider);
                CharacterDataSynchronizer.UpdateDataToTracking(container.outsider,container.outsider.getCharacterState());
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return true;
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        outsider.interactingPlayer=null;
    }

    @OnlyIn(Dist.CLIENT)
    public void requestOperation(OutsiderRequest operation) {
        VanityPacketHandler.CHANNEL.sendToServer(new OperationMsg(operation));
    }



    static class OperationMsg {
        String name;

        public OperationMsg(OutsiderRequest operation) {
            this.name = operation.getName();
        }

        public OperationMsg(String name) {
            this.name = name;
        }

        public static OperationMsg decoder(PacketBuffer buffer) {
            return new OperationMsg(buffer.readString());
        }

        public static void encoder(OperationMsg msg, PacketBuffer buffer) {
            buffer.writeString(msg.name);
        }
    }

}
