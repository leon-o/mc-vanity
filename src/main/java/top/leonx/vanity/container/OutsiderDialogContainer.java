package top.leonx.vanity.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.network.VanityPacketHandler;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OutsiderDialogContainer extends Container {
    static {
        VanityPacketHandler.registerMessage(3, OperationMsg.class, OperationMsg::encoder, OperationMsg::decoder, OutsiderDialogContainer::handler);
    }

    public OutsiderEntity outsider;
    PlayerEntity player;
    public PlayerEntity getPlayer() {
        return player;
    }

    public static final Operation FOLLOW_ME = Operation.create(container -> {
        if (container.player instanceof ServerPlayerEntity) container.outsider.setFollowedPlayer((ServerPlayerEntity) container.player);
    });

    public static final Operation DISBAND=Operation.create(container-> container.outsider.setFollowedPlayer(null));
    public static final Operation OPEN_INVENTORY=Operation.create(container->{
        INamedContainerProvider provider = new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return container.outsider.getDisplayName();
            }

            @Override
            public Container createMenu(int id, @Nonnull PlayerInventory inventory,@Nonnull PlayerEntity player) {
                return new OutsiderInventoryContainer(id,inventory,container.outsider);
            }
        };
        NetworkHooks.openGui((ServerPlayerEntity) container.player, provider, t->t.writeInt(container.outsider.getEntityId()));
    });
    public OutsiderDialogContainer(int windowId, PlayerInventory inv, PacketBuffer data) {
        this(windowId,inv,readOutsiderFromBuffer(data));
    }

    public OutsiderDialogContainer(int windowId, PlayerInventory inv, OutsiderEntity entity) {
        super(ModContainerTypes.OUTSIDER_DIALOG.get(), windowId);
        this.outsider = entity;
        player = inv.player;
    }
    private static OutsiderEntity readOutsiderFromBuffer(PacketBuffer data)
    {
        if (data != null && Minecraft.getInstance().world != null) {
            Entity entityByID = Minecraft.getInstance().world.getEntityByID(data.readInt());
            if (entityByID instanceof OutsiderEntity) return  (OutsiderEntity) entityByID;
        }

        return null;
    }
    private static void handler(OperationMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayerEntity sender = context.getSender();
            if (sender == null) return;
            if (sender.openContainer instanceof OutsiderDialogContainer) {
                OutsiderDialogContainer container = (OutsiderDialogContainer) sender.openContainer;
                Operation.getOperation(msg.opCode).execute.accept(container);
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
    public void requestOperation(Operation operation) {
        VanityPacketHandler.CHANNEL.sendToServer(new OperationMsg(operation));
    }

    public static class Operation {
        private final static Map<Integer, Operation> OPERATION_MAP   = new HashMap<>();
        private final static Operation               DUMMY_OPERATION = new Operation(-1, (t) -> {});
        int                               opCode;
        Consumer<OutsiderDialogContainer> execute;

        private Operation(int opCode, Consumer<OutsiderDialogContainer> execute) {
            this.opCode = opCode;
            this.execute = execute;
        }

        public static Operation create(Consumer<OutsiderDialogContainer> execute) {
            Operation operation = new Operation(OPERATION_MAP.size(), execute);
            OPERATION_MAP.put(operation.opCode, operation);
            return operation;
        }

        public static Operation getOperation(int id) {
            return OPERATION_MAP.getOrDefault(id, DUMMY_OPERATION);
        }
    }

    static class OperationMsg {
        int opCode;

        public OperationMsg(Operation operation) {
            this.opCode = operation.opCode;
        }

        public OperationMsg(int opCode) {
            this.opCode = opCode;
        }

        public static OperationMsg decoder(PacketBuffer buffer) {
            return new OperationMsg(buffer.readInt());
        }

        public static void encoder(OperationMsg msg, PacketBuffer buffer) {
            buffer.writeInt(msg.opCode);
        }
    }

}
