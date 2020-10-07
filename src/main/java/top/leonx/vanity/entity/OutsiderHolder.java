package top.leonx.vanity.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import top.leonx.vanity.data.OutsiderWorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutsiderHolder {
    private static OutsiderHolder logicalServerInstance;
    private static OutsiderHolder logicalClientInstance;
    public static OutsiderHolder getInstance()
    {
        if(Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
        {
            if(logicalServerInstance==null)
                logicalServerInstance=new OutsiderHolder();
            return logicalServerInstance;
        }else {
            if(logicalClientInstance==null)
                logicalClientInstance=new OutsiderHolder();

            return logicalClientInstance;
        }
    }
    private OutsiderHolder(){}
    public Map<UUID,OutsiderEntity>       onlineOutsiders        =new HashMap<>();
    public Map<UUID, OutsiderIncorporeal> outsiderIncorporealMap =new HashMap<>();

    public void joinWorld(OutsiderEntity outsider)
    {
        onlineOutsiders.put(outsider.getUniqueID(), outsider);
//        OutsiderIncorporeal outsiderIncorporeal = getOutsider(outsider.getUniqueID());
//        outsiderIncorporeal.bindTo(outsider); now bind in entity;
//        if(!outsider.world.isRemote())
//            OutsiderWorldSavedData.get(((ServerWorld) outsider.world)).setOutsiderComponent(outsider.getUniqueID(), outsiderIncorporeal.getComponent());
    }

    public void removedFromWorld(OutsiderEntity outsider)
    {
        onlineOutsiders.remove(outsider.getUniqueID());
//        OutsiderIncorporeal outsiderIncorporeal = getOutsider(outsider.getUniqueID());
//        outsiderIncorporeal.disbandTo(outsider);
//        if(!outsider.world.isRemote())
//            OutsiderWorldSavedData.get(((ServerWorld) outsider.world)).setOutsiderComponent(outsider.getUniqueID(), outsiderIncorporeal.getComponent());
    }

    public static void onWorldLoaded(WorldEvent.Load event)
    {
        if(!(event.getWorld() instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) event.getWorld();
        OutsiderWorldSavedData outsiderWorldSavedData = OutsiderWorldSavedData.get(world);
        outsiderWorldSavedData.getOfflineOutsiderMap().forEach((k, v)->{
            OutsiderIncorporeal outsider = new OutsiderIncorporeal(k);
            outsider.setEntityComponent(v);
            getInstance().outsiderIncorporealMap.put(k, outsider);
        });
    }

    public OutsiderIncorporeal createOfflineCopy(OutsiderEntity entity)
    {
        OutsiderIncorporeal outsider = new OutsiderIncorporeal(entity.getUniqueID());
        outsider.setEntityComponent(entity.writeWithoutTypeId(new CompoundNBT()));
        return outsider;
    }

    public OutsiderIncorporeal getOutsider(UUID uuid)
    {
        return outsiderIncorporealMap.computeIfAbsent(uuid, OutsiderIncorporeal::new);
    }
}
