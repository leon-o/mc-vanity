package top.leonx.vanity.container;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModContainerTypes;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BedContainer extends Container {
    public PlayerEntity player;
    VanityBedTileEntity tileEntity;
    public List<OutsiderEntity> entities;
    public BedContainer(int windowId, PlayerInventory inventory, VanityBedTileEntity tileEntity) {
        super(ModContainerTypes.VANITY_BED_CONTAINER.get(), windowId);
        this.player = inventory.player;
        this.tileEntity=tileEntity;
        CharacterState characterState = player.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
        World world = inventory.player.world;
        if(world instanceof ClientWorld)
        {
            entities=new ArrayList<>();
            ((ClientWorld) world).getAllEntities().forEach(t->{
                if(!(t instanceof OutsiderEntity) || characterState.getRelationWith(t.getUniqueID())<20) return;
                entities.add(((OutsiderEntity) t));
            });
        }else if(world instanceof ServerWorld){
            entities=((ServerWorld) world).getEntities(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.get(),t-> characterState.getRelationWith(t.getUniqueID())>=20).stream().map(t-> ((OutsiderEntity) t)).collect(
                        Collectors.toList());
        }
    }
    public BedContainer(int windowId, PlayerInventory inv, PacketBuffer data)
    {
        this(windowId,inv,(VanityBedTileEntity)inv.player.world.getTileEntity(data.readBlockPos()));
    }


    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
