package top.leonx.vanity.entity;

import com.mojang.datafixers.util.Either;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutsiderHolder {
    public static Map<UUID,OutsiderEntity> onlineOutsiders =new HashMap<>();
    public static Map<UUID,OfflineOutsider> offlineOutsiders =new HashMap<>();

    public static void joinWorld(OutsiderEntity outsider)
    {
        OutsiderHolder.onlineOutsiders.put(outsider.getUniqueID(), outsider);
        if(!offlineOutsiders.containsKey(outsider.getUniqueID()))
            OutsiderHolder.offlineOutsiders.put(outsider.getUniqueID(),createOfflineCopy(outsider));
        else
            offlineOutsiders.get(outsider.getUniqueID()).bindTo(outsider);
    }

    public static void removedFromWorld(OutsiderEntity outsider)
    {
        OutsiderHolder.onlineOutsiders.remove(outsider.getUniqueID());
    }

    public static OfflineOutsider createOfflineCopy(OutsiderEntity entity)
    {
        return new OfflineOutsider(entity);
    }

    public static OfflineOutsider getOutsider(UUID uuid)
    {
        return offlineOutsiders.get(uuid);
    }
}
