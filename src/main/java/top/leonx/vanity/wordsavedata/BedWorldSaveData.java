package top.leonx.vanity.wordsavedata;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class BedWorldSaveData extends WorldSavedData {
    public Multimap<UUID, BlockPos> entityBedsMap =HashMultimap.create();
    public void addBedPos(UUID entityId,BlockPos pos)
    {
        entityBedsMap.put(entityId,pos);
        markDirty();
    }
    public void removeBedPos(UUID entityId,BlockPos pos){
        entityBedsMap.remove(entityId,pos);
        markDirty();
    }
    public BedWorldSaveData() {
        super("vanity_bed");
    }

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT keys = nbt.getList("keys", 8); //8-> StringNbt
        for (int i = 0; i < keys.size(); i++) {
            String uuidString = keys.getString(i);
            UUID entityID= UUID.fromString(uuidString);
            long[] blockPosLongArray  = nbt.getLongArray(uuidString);
            for (long l : blockPosLongArray) {
                entityBedsMap.put(entityID,BlockPos.fromLong(l));
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt=new CompoundNBT();
        ListNBT keyList=new ListNBT();
        for (UUID key : entityBedsMap.keys()) {
            keyList.add(StringNBT.valueOf(key.toString()));

            LongArrayNBT longArrayNBT=new LongArrayNBT(entityBedsMap.get(key).stream().map(BlockPos::toLong).collect(Collectors.toList()));
            nbt.put(key.toString(),longArrayNBT);
        }
        nbt.put("keys",keyList);
        return nbt;
    }

    public static BedWorldSaveData get(ServerWorld world)
    {
        return world.getSavedData().getOrCreate(BedWorldSaveData::new,"vanity_bed");
    }
}
