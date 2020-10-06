package top.leonx.vanity.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import top.leonx.vanity.data.IncorporealDataManager;
import top.leonx.vanity.entity.OutsiderIncorporeal;
import top.leonx.vanity.entity.OutsiderHolder;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class OutsiderIncorporealSynchronizer {
    public static void register() {
        if (FMLEnvironment.dist.isClient()) {
            VanityPacketHandler.registerMessage(OfflineOutsiderMsg.class, OfflineOutsiderMsg::encode, OfflineOutsiderMsg::decode, OutsiderIncorporealSynchronizer::handlerClient);
        } else {
            VanityPacketHandler.registerMessage(OfflineOutsiderMsg.class, OfflineOutsiderMsg::encode,
                                                OfflineOutsiderMsg::decode, OutsiderIncorporealSynchronizer::handlerServer);
        }
    }

    private static void handlerServer(OfflineOutsiderMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
    }

    private static void handlerClient(OfflineOutsiderMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
            OutsiderIncorporeal outsiderEither = OutsiderHolder.getInstance().getOutsider(msg.targetUUID);
            if(outsiderEither==null) return;
            outsiderEither.getDataManager().setEntryValues(msg.dataEntries);
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static class OfflineOutsiderMsg{
        public UUID                                 targetUUID;
        public List<EntityDataManager.DataEntry<?>> dataEntries;

        public OfflineOutsiderMsg(IncorporealDataManager dataManager, UUID targetUUID) {
            this(dataManager.getDirty(),targetUUID);
        }
        public OfflineOutsiderMsg(List<EntityDataManager.DataEntry<?>> entries, UUID targetUUID) {
            this.dataEntries=entries;
            this.targetUUID = targetUUID;
        }

        public static void encode(OfflineOutsiderMsg msg, PacketBuffer buffer) {
            try {
                buffer.writeUniqueId(msg.targetUUID);
                IncorporealDataManager.writeEntries(msg.dataEntries, buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static OfflineOutsiderMsg decode(PacketBuffer buffer) {
            try {
                UUID targetUUID=buffer.readUniqueId();
                List<EntityDataManager.DataEntry<?>> dataEntries = IncorporealDataManager.readEntries(buffer);
                return new OfflineOutsiderMsg(dataEntries,targetUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
