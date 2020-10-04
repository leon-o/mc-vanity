package top.leonx.vanity.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.datasync.IDataSerializer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.leonx.vanity.entity.OfflineOutsider;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class OfflineDataManager {
    private static final Logger                                LOGGER      = LogManager.getLogger();
    private static final  Map<Class<? extends Entity>, Integer> NEXT_ID_MAP = Maps.newHashMap();
    private final         Map<Integer, EntityDataManager.DataEntry<?>> entries     = Maps.newHashMap();
    private final         ReadWriteLock                                lock        = new ReentrantReadWriteLock();
    private               boolean                                      empty       = true;
    private               boolean                                      dirty;
    private final OfflineOutsider outsider;
    public OfflineDataManager(OfflineOutsider outsider) {
        this.outsider=outsider;
    }

    public static <T> DataParameter<T> createKey(Class<? extends Entity> clazz, IDataSerializer<T> serializer) {
        // Forge: This is very useful for mods that register keys on classes that are not their own
        try {
            Class<?> oclass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName());
            if (!oclass.equals(clazz)) {
                // Forge: log at warn, mods should not add to classes that they don't own, and only add stacktrace when in debug is enabled as it is mostly not needed and consumes time
                if (LOGGER.isDebugEnabled()) LOGGER.warn("defineId called for: {} from {}", clazz, oclass, new RuntimeException());
                else LOGGER.warn("defineId called for: {} from {}", clazz, oclass);
            }
        } catch (ClassNotFoundException ignored) {
        }

        int j;
        if (NEXT_ID_MAP.containsKey(clazz)) {
            j = NEXT_ID_MAP.get(clazz) + 1;
        } else {
            int i = 0;
            Class<?> oclass1 = clazz;

            while(oclass1 != Entity.class) {
                oclass1 = oclass1.getSuperclass();
                if (NEXT_ID_MAP.containsKey(oclass1)) {
                    i = NEXT_ID_MAP.get(oclass1) + 1;
                    break;
                }
            }

            j = i;
        }

        if (j > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + j + "! (Max is " + 254 + ")");
        } else {
            NEXT_ID_MAP.put(clazz, j);
            return serializer.createKey(j);
        }
    }

    public <T> void register(DataParameter<T> key, T value) {
        int i = key.getId();
        if (i > 254) {
            throw new IllegalArgumentException("Data value id is too big with " + i + "! (Max is " + 254 + ")");
        } else if (this.entries.containsKey(i)) {
            throw new IllegalArgumentException("Duplicate id value for " + i + "!");
        } else if (DataSerializers.getSerializerId(key.getSerializer()) < 0) {
            throw new IllegalArgumentException("Unregistered serializer " + key.getSerializer() + " for " + i + "!");
        } else {
            this.setEntry(key, value);
        }
    }
    public <T> boolean hasKey(DataParameter<T> key)
    {
        return this.entries.containsKey(key.getId());
    }
    private <T> void setEntry(DataParameter<T> key, T value) {
        EntityDataManager.DataEntry<T> dataEntry = new EntityDataManager.DataEntry<>(key, value);
        this.lock.writeLock().lock();
        this.entries.put(key.getId(), dataEntry);
        this.empty = false;
        this.lock.writeLock().unlock();
    }

    private <T> EntityDataManager.DataEntry<T> getEntry(DataParameter<T> key) {
        this.lock.readLock().lock();

        EntityDataManager.DataEntry<T> dataEntry;
        try {
            dataEntry = (EntityDataManager.DataEntry<T>) this.entries.get(key.getId());
        } catch (Throwable throwable) {
            CrashReport         crashreport         = CrashReport.makeCrashReport(throwable, "Getting synched entity data");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Synched entity data");
            crashreportcategory.addDetail("Data ID", key);
            throw new ReportedException(crashreport);
        } finally {
            this.lock.readLock().unlock();
        }

        return dataEntry;
    }

    public <T> T get(DataParameter<T> key) {
        return this.getEntry(key).getValue();
    }

    public <T> void set(DataParameter<T> key, T value) {
        EntityDataManager.DataEntry<T> dataEntry = this.getEntry(key);
        if (ObjectUtils.notEqual(value, dataEntry.getValue())) {
            dataEntry.setValue(value);
            this.outsider.notifyDataManagerChange(key);
            dataEntry.setDirty(true);
            this.dirty = true;
        }

    }

    public boolean isDirty() {
        return this.dirty;
    }

    public static void writeEntries(List<EntityDataManager.DataEntry<?>> entriesIn, PacketBuffer buf) throws IOException {
        if (entriesIn != null) {
            int i = 0;

            for(int j = entriesIn.size(); i < j; ++i) {
                writeEntry(buf, entriesIn.get(i));
            }
        }

        buf.writeByte(255);
    }

    @Nullable
    public List<EntityDataManager.DataEntry<?>> getDirty() {
        List<EntityDataManager.DataEntry<?>> list = null;
        if (this.dirty) {
            this.lock.readLock().lock();

            for(EntityDataManager.DataEntry<?> dataEntry : this.entries.values()) {
                if (dataEntry.isDirty()) {
                    dataEntry.setDirty(false);
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(dataEntry.copy());
                }
            }

            this.lock.readLock().unlock();
        }

        this.dirty = false;
        return list;
    }

    @Nullable
    public List<EntityDataManager.DataEntry<?>> getAll() {
        List<EntityDataManager.DataEntry<?>> list = null;
        this.lock.readLock().lock();

        for(EntityDataManager.DataEntry<?> dataEntry : this.entries.values()) {
            if (list == null) {
                list = Lists.newArrayList();
            }

            list.add(dataEntry.copy());
        }

        this.lock.readLock().unlock();
        return list;
    }

    private static <T> void writeEntry(PacketBuffer buf, EntityDataManager.DataEntry<T> entry) throws IOException {
        DataParameter<T> dataParameter = entry.getKey();
        int i = DataSerializers.getSerializerId(dataParameter.getSerializer());
        if (i < 0) {
            throw new EncoderException("Unknown serializer type " + dataParameter.getSerializer());
        } else {
            buf.writeByte(dataParameter.getId());
            buf.writeVarInt(i);
            dataParameter.getSerializer().write(buf, entry.getValue());
        }
    }

    @Nullable
    public static List<EntityDataManager.DataEntry<?>> readEntries(PacketBuffer buf) throws IOException {
        List<EntityDataManager.DataEntry<?>> list = null;

        int i;
        while((i = buf.readUnsignedByte()) != 255) {
            if (list == null) {
                list = Lists.newArrayList();
            }

            int j = buf.readVarInt();
            IDataSerializer<?> iDataSerializer = DataSerializers.getSerializer(j);
            if (iDataSerializer == null) {
                throw new DecoderException("Unknown serializer type " + j);
            }

            list.add(makeDataEntry(buf, i, iDataSerializer));
        }

        return list;
    }

    private static <T> EntityDataManager.DataEntry<T> makeDataEntry(PacketBuffer bufferIn, int idIn, IDataSerializer<T> serializerIn) {
        return new EntityDataManager.DataEntry<>(serializerIn.createKey(idIn), serializerIn.read(bufferIn));
    }

    @OnlyIn(Dist.CLIENT)
    public void setEntryValues(List<EntityDataManager.DataEntry<?>> entriesIn) {
        this.lock.writeLock().lock();

        for(EntityDataManager.DataEntry<?> dataEntry : entriesIn) {
            EntityDataManager.DataEntry<?> dataEntry1 = this.entries.get(dataEntry.getKey().getId());
            if (dataEntry1 != null) {
                this.setEntryValue(dataEntry1, dataEntry);
                this.outsider.notifyDataManagerChange(dataEntry.getKey());
            }
        }

        this.lock.writeLock().unlock();
        this.dirty = true;
    }

    @OnlyIn(Dist.CLIENT)
    private <T> void setEntryValue(EntityDataManager.DataEntry<T> target, EntityDataManager.DataEntry<?> source) {
        if (!Objects.equals(source.getKey().getSerializer(), target.getKey().getSerializer())) {
            throw new IllegalStateException(String.format("Invalid entity data item type for field %d on entity %s: old=%s(%s), new=%s(%s)", target.getKey().getId(), this.outsider, target.getValue(),
                                                          target.getValue().getClass(), source.getValue(), source.getValue().getClass()));
        } else {
            target.setValue((T)source.getValue());
        }
    }

    public boolean isEmpty() {
        return this.empty;
    }

    public void setClean() {
        this.dirty = false;
        this.lock.readLock().lock();

        for(EntityDataManager.DataEntry<?> dataEntry : this.entries.values()) {
            dataEntry.setDirty(false);
        }

        this.lock.readLock().unlock();
    }
}
