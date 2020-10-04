package top.leonx.vanity.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import top.leonx.vanity.network.OfflineDataManager;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.UUID;

public class OfflineOutsider extends CapabilityProvider<OfflineOutsider> {
    OfflineDataManager dataManager = new OfflineDataManager(this);
    UUID    realUniqueId;
    boolean isChild;
    private WeakReference<OutsiderEntity> outsiderEntity;
    private CompoundNBT                   entityComponent = null;
    private boolean                       dirty           = false;

    public OfflineOutsider(UUID uuid) {
        super(OfflineOutsider.class);
        this.gatherCapabilities();
        this.realUniqueId = uuid;
        //bindTo(entity);
    }

    public CompoundNBT getEntityComponent() {
        return entityComponent;
    }

    public void setEntityComponent(CompoundNBT entityComponent) {
        CompoundNBT capsNbt=entityComponent.getCompound("ForgeCaps");
        if(capsNbt.size()>0)
            deserializeCaps(capsNbt);
        this.entityComponent = entityComponent;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void bindTo(OutsiderEntity entity) {
        outsiderEntity = new WeakReference<>(entity);

        realUniqueId = entity.getUniqueID();
        isChild = entity.isChild();

        List<EntityDataManager.DataEntry<?>> allEntries = entity.getDataManager().getAll();
        if (allEntries != null) for (EntityDataManager.DataEntry dataEntry : allEntries) {
            if (dataManager.hasKey(dataEntry.getKey())) dataManager.set(dataEntry.getKey(), dataEntry.getValue());
            else dataManager.register(dataEntry.getKey(), dataEntry.getValue());
        }
        if(entityComponent==null)
            setEntityComponent(entity.writeWithoutTypeId(new CompoundNBT()));
    }
    public void disbandTo(OutsiderEntity outsider) {
        setEntityComponent(outsider.writeWithoutTypeId(new CompoundNBT()));
    }
    public boolean isInWorld() {
        return outsiderEntity.get() != null;
    }

    public OutsiderEntity getOutsiderEntity() {
        return outsiderEntity.get();
    }

    public OfflineDataManager getDataManager() {
        return dataManager;
    }

    public boolean isChild() {
        return isChild;
    }

    public void setChild(boolean child) {
        isChild = child;
    }

    public UUID getRealUniqueId() {
        return realUniqueId;
    }

    public <T> void notifyDataManagerChange(DataParameter<T> key) {

    }

    public boolean isDirty() {
        return dirty;
    }

    public void markDarty() {
        dirty = true;
    }

    public String getCustomName()
    {
        return entityComponent.getString("CustomName");
    }
}
