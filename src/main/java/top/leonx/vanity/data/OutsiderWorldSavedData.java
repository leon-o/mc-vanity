package top.leonx.vanity.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OutsiderWorldSavedData extends WorldSavedData {
    static final String                 NAME               ="outsider";
    private final Map<UUID,CompoundNBT> offlineOutsiderMap =new HashMap<>();
    public OutsiderWorldSavedData() {
        super(NAME);
    }
    public CompoundNBT getOutsiderComponent(UUID uuid)
    {
        return offlineOutsiderMap.get(uuid);
    }
    public void remove(UUID uuid)
    {
        offlineOutsiderMap.remove(uuid);
        markDirty();
    }
    public void setOutsiderComponent(UUID uuid,CompoundNBT nbt)
    {
        offlineOutsiderMap.put(uuid, nbt);
        markDirty();
    }

    public Map<UUID, CompoundNBT> getOfflineOutsiderMap() {
        return offlineOutsiderMap;
    }

    @Override
    public void read(CompoundNBT nbt) {
        offlineOutsiderMap.clear();
        ListNBT outsiders = nbt.getList("outsiders", 10);
        for (INBT outsider : outsiders) {
            CompoundNBT compoundNBT = (CompoundNBT) outsider;
            UUID uuid = compoundNBT.getUniqueId("UUID");
            offlineOutsiderMap.put(uuid, compoundNBT);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT outsiders = new ListNBT();
        offlineOutsiderMap.forEach((key, value)->{
            outsiders.add(value);
        });
        CompoundNBT compoundNBT=new CompoundNBT();
        compoundNBT.put("outsiders",outsiders);
        return compoundNBT;
    }

    public static OutsiderWorldSavedData get(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(OutsiderWorldSavedData::new,NAME);
    }
}
