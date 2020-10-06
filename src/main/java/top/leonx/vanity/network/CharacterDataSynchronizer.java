package top.leonx.vanity.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.init.ModCapabilityTypes;

import java.util.function.Supplier;

public class CharacterDataSynchronizer {
    public static void register() {
        if (FMLEnvironment.dist.isClient()) {
            VanityPacketHandler.registerMessage(CharacterStateMsg.class, CharacterStateMsg::encode,
                                                CharacterStateMsg::decode,
                                                CharacterDataSynchronizer::handlerClient);
        } else {
            VanityPacketHandler.registerMessage(CharacterStateMsg.class, CharacterStateMsg::encode,
                                                CharacterStateMsg::decode, CharacterDataSynchronizer::handlerServer);
        }

    }

    public static void UpdateDataToClient(ServerPlayerEntity playerEntity, CharacterState data, int targetId) {
        VanityPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new CharacterDataSynchronizer.CharacterStateMsg(data, targetId));
    }

    public static void UpdateDataToTracking(Entity entity, CharacterState data) {
        if(entity.world.isRemote())
        {
            VanityMod.LOGGER.warn("Can't send package to tracked players from client side");
            return;
        }
        VanityPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new CharacterDataSynchronizer.CharacterStateMsg(data, entity.getEntityId()));
    }

    public static void RequireServerToUpdate(CharacterState data, int targetId) {
        VanityPacketHandler.CHANNEL.sendToServer(new CharacterDataSynchronizer.CharacterStateMsg(data, targetId));
    }

    @OnlyIn(Dist.CLIENT)
    // client handler
    private static void handlerClient(CharacterDataSynchronizer.CharacterStateMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> {
            World world = playerEntity == null ? Minecraft.getInstance().world : playerEntity.getEntityWorld();
            handlerCommon(world, msg, playerEntity != null);
        });

        contextSupplier.get().setPacketHandled(true);
    }

    static void handlerServer(CharacterDataSynchronizer.CharacterStateMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> {
            if (playerEntity == null) return;
            World world = playerEntity.getEntityWorld();
            handlerCommon(world, msg, true);

        });

        contextSupplier.get().setPacketHandled(true);
    }

    private static void handlerCommon(World world, CharacterDataSynchronizer.CharacterStateMsg msg, boolean serverside) {
        if (world == null) return;
        Entity entity = world.getEntityByID(msg.targetId);
        if (entity instanceof LivingEntity) {
            CharacterState bodyPartData = entity.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
            bodyPartData.setRoot(msg.root);
            if (serverside) UpdateDataToTracking(entity, bodyPartData);
        }
    }

    public static class CharacterStateMsg {
        public int targetId;
        public CompoundNBT root;

        public CharacterStateMsg() {
            root = new CompoundNBT();
        }

        public CharacterStateMsg(CharacterState characterState, int targetId) {
            root=characterState.getRoot();
            this.targetId = targetId;
        }


        public static void encode(CharacterStateMsg msg, PacketBuffer buffer) {
            buffer.writeInt(msg.targetId);
            buffer.writeCompoundTag(msg.root);
        }

        public static CharacterStateMsg decode(PacketBuffer buffer) {
            CharacterStateMsg msg = new CharacterStateMsg();
            msg.targetId = buffer.readInt();
            msg.root=buffer.readCompoundTag();

            return msg;
        }
    }
}
