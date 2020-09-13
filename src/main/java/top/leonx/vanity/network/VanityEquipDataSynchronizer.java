package top.leonx.vanity.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.bodypart.AbstractBodyPart;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartRegistry;
import top.leonx.vanity.bodypart.BodyPartStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class VanityEquipDataSynchronizer {

    public static void register() {
        if (FMLEnvironment.dist.isClient()) {
            VanityPacketHandler.registerMessage(0, UpdateVanityEquipDataMsg.class, UpdateVanityEquipDataMsg::encode, UpdateVanityEquipDataMsg::decode, VanityEquipDataSynchronizer::handlerClient);
        } else {
            VanityPacketHandler.registerMessage(0, UpdateVanityEquipDataMsg.class, UpdateVanityEquipDataMsg::encode, UpdateVanityEquipDataMsg::decode, VanityEquipDataSynchronizer::handlerServer);
        }

    }

    public static void UpdateDataToClient(ServerPlayerEntity playerEntity, BodyPartCapability.BodyPartData data, int targetId) {
        VanityPacketHandler.CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerEntity), new UpdateVanityEquipDataMsg(data, targetId));
    }

    public static void UpdateDataToTracking(Entity entity, BodyPartCapability.BodyPartData data, int targetId) {
        VanityPacketHandler.CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new UpdateVanityEquipDataMsg(data, targetId));
    }

    public static void RequireServerToUpdate(BodyPartCapability.BodyPartData data, int targetId) {
        VanityPacketHandler.CHANNEL.sendToServer(new UpdateVanityEquipDataMsg(data, targetId));
    }

    @OnlyIn(Dist.CLIENT)
    // client handler
    private static void handlerClient(UpdateVanityEquipDataMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> {
            World world = playerEntity == null ? Minecraft.getInstance().world : playerEntity.getEntityWorld();
            handlerCommon(world, msg, playerEntity != null);
        });

        contextSupplier.get().setPacketHandled(true);
    }

    private static void handlerServer(UpdateVanityEquipDataMsg msg, Supplier<NetworkEvent.Context> contextSupplier) {
        ServerPlayerEntity playerEntity = contextSupplier.get().getSender();
        contextSupplier.get().enqueueWork(() -> {
            if (playerEntity == null) return;
            World world = playerEntity.getEntityWorld();
            handlerCommon(world, msg, true);

        });

        contextSupplier.get().setPacketHandled(true);
    }

    private static void handlerCommon(World world, UpdateVanityEquipDataMsg msg, boolean serverside) {
        if (world == null) return;
        Entity entity = world.getEntityByID(msg.targetId);
        if (entity instanceof LivingEntity) {
            BodyPartCapability.BodyPartData bodyPartData = entity.getCapability(ModCapabilityTypes.BODY_PART).orElse(new BodyPartCapability.BodyPartData());
            bodyPartData.getItemStacksList().clear();
            bodyPartData.getItemStacksList().addAll(msg.itemStacks);
            bodyPartData.setNeedInit(false );
            if (serverside) UpdateDataToTracking(entity, bodyPartData, entity.getEntityId());
        }
    }

    public static class UpdateVanityEquipDataMsg {
        public int                 targetId;
        public List<BodyPartStack> itemStacks;

        public UpdateVanityEquipDataMsg() {
            itemStacks = new ArrayList<>();
        }

        public UpdateVanityEquipDataMsg(BodyPartCapability.BodyPartData bodyPartData, int targetId) {
            itemStacks = bodyPartData.getItemStacksList();
            this.targetId = targetId;
        }


        public static void encode(UpdateVanityEquipDataMsg msg, PacketBuffer buffer) {
            buffer.writeInt(msg.targetId);

            // group name, body part location, color
            List<String> groupNames=new ArrayList<>();
            List<ResourceLocation> locations=new ArrayList<>();
            List<Integer> colors      =new ArrayList<>();
            List<Map<String,Float>>    attributes =new ArrayList<>();
            msg.itemStacks.forEach(t -> {
                ResourceLocation location = BodyPartRegistry.getLocation(t.getItem());
                if (location != null)
                {
                    groupNames.add(t.getItem().getGroup().getName());
                    locations.add(location);
                    colors.add(t.getColor());
                    attributes.add(t.getAdjustableAttributes());
                }
            });
            buffer.writeInt(groupNames.size());
            for (int i=0;i<groupNames.size();i++)
            {
                buffer.writeString(groupNames.get(i));
                buffer.writeString(locations.get(i).getNamespace());
                buffer.writeString(locations.get(i).getPath());
                buffer.writeInt(colors.get(i));
                Map<String, Float> attribtueMap = attributes.get(i);
                buffer.writeInt(attribtueMap.size());
                for (Map.Entry<String, Float> entry : attribtueMap.entrySet()) {
                    buffer.writeString(entry.getKey());
                    buffer.writeFloat(entry.getValue());
                }
            }
        }

        public static UpdateVanityEquipDataMsg decode(PacketBuffer buffer) {
            UpdateVanityEquipDataMsg msg = new UpdateVanityEquipDataMsg();
            msg.targetId = buffer.readInt();

            int itemsCount = buffer.readInt();
            for (int i = 0; i < itemsCount; i++) {
                BodyPartGroup    group    = BodyPartGroup.getGroupByName(buffer.readString());
                ResourceLocation location = new ResourceLocation(buffer.readString(), buffer.readString());
                int              color    = buffer.readInt();
                AbstractBodyPart item = BodyPartRegistry.getBodyPart(group, location);
                int mapSize=buffer.readInt();
                Map<String,Float> attributeMap=new HashMap<>();
                for(int j=0;j<mapSize;j++)
                {
                    attributeMap.put(buffer.readString(),buffer.readFloat());
                }
                if(item==null) continue;
                msg.itemStacks.add(new BodyPartStack(item, color,attributeMap));
            }

            return msg;
        }
    }
}
