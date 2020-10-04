package top.leonx.vanity.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import top.leonx.vanity.data.OutsiderWorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutsiderHolder {
    public static Map<UUID,OutsiderEntity> onlineOutsiders =new HashMap<>();
    public static Map<UUID,OfflineOutsider> offlineOutsiders =new HashMap<>();

    public static void joinWorld(OutsiderEntity outsider)
    {
        OutsiderHolder.onlineOutsiders.put(outsider.getUniqueID(), outsider);
        OfflineOutsider offlineOutsider = getOutsider(outsider.getUniqueID());
        offlineOutsider.bindTo(outsider);
        if(!outsider.world.isRemote())
            OutsiderWorldSavedData.get(((ServerWorld) outsider.world)).setOutsiderComponent(outsider.getUniqueID(), offlineOutsider.getEntityComponent());
    }

    public static void removedFromWorld(OutsiderEntity outsider)
    {
        OutsiderHolder.onlineOutsiders.remove(outsider.getUniqueID());
        OfflineOutsider offlineOutsider = getOutsider(outsider.getUniqueID());
        offlineOutsider.disbandTo(outsider);
        if(!outsider.world.isRemote())
            OutsiderWorldSavedData.get(((ServerWorld) outsider.world)).setOutsiderComponent(outsider.getUniqueID(),offlineOutsider.getEntityComponent());
    }

    public static void onWorldLoaded(WorldEvent.Load event)
    {
        if(!(event.getWorld() instanceof ServerWorld)) return;

        ServerWorld world = (ServerWorld) event.getWorld();
        OutsiderWorldSavedData outsiderWorldSavedData = OutsiderWorldSavedData.get(world);
        outsiderWorldSavedData.getOfflineOutsiderMap().forEach((k, v)->{
            OfflineOutsider outsider = new OfflineOutsider(k);
            outsider.setEntityComponent(v);
            offlineOutsiders.put(k,outsider);
        });
    }

    public static OfflineOutsider createOfflineCopy(OutsiderEntity entity)
    {
        OfflineOutsider outsider = new OfflineOutsider(entity.getUniqueID());
        outsider.setEntityComponent(entity.writeWithoutTypeId(new CompoundNBT()));
        return outsider;
    }
/*    private static void addOutsider(OutsiderEntity entity)
    {
        OfflineOutsider outsider = createOfflineCopy(entity);
        outsiderWorldSavedData.setOutsiderComponent(entity.getUniqueID(),outsider.getEntityComponent());
        offlineOutsiders.put(entity.getUniqueID(),outsider);
    }*/
    public static OfflineOutsider getOutsider(UUID uuid)
    {
        return offlineOutsiders.computeIfAbsent(uuid, OfflineOutsider::new);
    }
}
