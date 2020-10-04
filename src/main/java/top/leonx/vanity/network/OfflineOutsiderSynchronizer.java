package top.leonx.vanity.network;

import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OfflineOutsider;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.entity.OutsiderHolder;

import java.io.IOException;
import java.lang.annotation.Target;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class OfflineOutsiderSynchronizer {
    public static void register() {
        if (FMLEnvironment.dist.isClient()) {
            VanityPacketHandler.registerMessage(3, OfflineOutsiderMsg.class, OfflineOutsiderMsg::encode, OfflineOutsiderMsg::decode, OfflineOutsiderSynchronizer::handlerClient);
        } else {
            VanityPacketHandler.registerMessage(3, OfflineOutsiderMsg.class, OfflineOutsiderMsg::encode,
                                                OfflineOutsiderMsg::decode, OfflineOutsiderSynchronizer::handlerServer);
        }
    }

    private static void handlerServer(OfflineOutsiderMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
    }

    private static void handlerClient(OfflineOutsiderMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
            Either<OutsiderEntity, OfflineOutsider> outsiderEither = OutsiderHolder.getOutsider(msg.targetUUID);
            if(outsiderEither==null) return;
            outsiderEither.ifRight(outsider->{
                outsider.getDataManager().setEntryValues(msg.dataEntries);
            });
        });
        contextSupplier.get().setPacketHandled(true);
    }

    public static class OfflineOutsiderMsg{
        public UUID                                 targetUUID;
        public List<EntityDataManager.DataEntry<?>> dataEntries;

        public OfflineOutsiderMsg(OfflineDataManager dataManager, UUID targetUUID) {
            this(dataManager.getDirty(),targetUUID);
        }
        public OfflineOutsiderMsg(List<EntityDataManager.DataEntry<?>> entries, UUID targetUUID) {
            this.dataEntries=entries;
            this.targetUUID = targetUUID;
        }

        public static void encode(OfflineOutsiderMsg msg, PacketBuffer buffer) {
            try {
                buffer.writeUniqueId(msg.targetUUID);
                OfflineDataManager.writeEntries(msg.dataEntries,buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static OfflineOutsiderMsg decode(PacketBuffer buffer) {
            try {
                UUID targetUUID=buffer.readUniqueId();
                List<EntityDataManager.DataEntry<?>> dataEntries = OfflineDataManager.readEntries(buffer);
                return new OfflineOutsiderMsg(dataEntries,targetUUID);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
