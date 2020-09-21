package top.leonx.vanity.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.network.VanityEquipDataSynchronizer;
import top.leonx.vanity.tileentity.VanityMirrorTileEntity;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.bodypart.BodyPartStack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class VanityMirrorContainer extends Container {
    public Map<BodyPartGroup, Queue<BodyPartStack>> selectedVanityItems =new HashMap<>();
    PlayerEntity                    player;
    BodyPartCapability.BodyPartData originBodyPartData;
    public VanityMirrorContainer(int windowId, PlayerInventory inventory, VanityMirrorTileEntity tileEntity) {
        super(ModContainerTypes.VANITY_MIRROR_CONTAINER.get(), windowId);
        player = inventory.player;

        BodyPartCapability.BodyPartData bodyPartData = player.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        originBodyPartData =new BodyPartCapability.BodyPartData();
        originBodyPartData.getItemStacksList().addAll(bodyPartData.getItemStacksList());

        for (BodyPartStack stack : bodyPartData.getItemStacksList()) {
            Queue<BodyPartStack> queue = selectedVanityItems.computeIfAbsent(stack.getItem().getGroup(), t -> new LinkedList<>());
            queue.add(stack);
        }
    }
    public VanityMirrorContainer(int windowId, PlayerInventory inv, PacketBuffer data)
    {
        this(windowId,inv,(VanityMirrorTileEntity)inv.player.world.getTileEntity(data.readBlockPos()));
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void updateToServer()
    {
        BodyPartCapability.BodyPartData bodyPartData = player.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        VanityEquipDataSynchronizer.RequireServerToUpdate(bodyPartData, player.getEntityId());
        applied=true;
        player.closeScreen();
    }
    private boolean applied=false;
    public void applyToClientPlayer() {
        BodyPartCapability.BodyPartData bodyPartData = player.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        bodyPartData.getItemStacksList().clear();
        for (Queue<BodyPartStack> queue : selectedVanityItems.values()) {
            for (BodyPartStack item : queue) {
                bodyPartData.getItemStacksList().add(item);
            }
        }
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        if( !playerIn.world.isRemote ||applied)return;
        BodyPartCapability.BodyPartData bodyPartData = playerIn.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        bodyPartData.getItemStacksList().clear();
        bodyPartData.getItemStacksList().addAll(originBodyPartData.getItemStacksList());
    }
}
