package top.leonx.vanity.container;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.init.ModEntityTypes;

public class OutsiderContainer extends Container {
    public OutsiderEntity outsider;
    public OutsiderContainer(int windowId, PlayerInventory inv, PacketBuffer data)
    {
        this(windowId, inv, new OutsiderEntity(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE,inv.player.world));
        if(data!=null && Minecraft.getInstance().world!=null)
        {
            Entity entityByID = Minecraft.getInstance().world.getEntityByID(data.readInt());
            if(entityByID instanceof OutsiderEntity)
                outsider =(OutsiderEntity) entityByID;
        }

    }
    public OutsiderContainer(int windowId, PlayerInventory inv, OutsiderEntity entity)
    {
        super(ModContainerTypes.OUTSIDER,windowId);
        this.outsider =entity;
    }
    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
