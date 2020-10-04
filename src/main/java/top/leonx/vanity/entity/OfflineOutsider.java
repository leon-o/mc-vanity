package top.leonx.vanity.entity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import top.leonx.vanity.network.OfflineDataManager;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class OfflineOutsider extends CapabilityProvider<OfflineOutsider> {
    OfflineDataManager dataManager;
    UUID               realUniqueId;
    boolean isChild;
    private WeakReference<OutsiderEntity> outsiderEntity;
    public OfflineOutsider(OutsiderEntity entity)
    {
        super(OfflineOutsider.class);
        this.gatherCapabilities();
        bindTo(entity);
    }

    public void bindTo(OutsiderEntity entity)
    {
        isChild=entity.isChild();
        dataManager=new OfflineDataManager(this);
        for (EntityDataManager.DataEntry dataEntry : entity.getDataManager().getAll()) {
            dataManager.register(dataEntry.getKey(),dataEntry.getValue());
        }
        CompoundNBT nbt = entity.getSerializedCaps();
        deserializeCaps(nbt);
        realUniqueId=entity.getUniqueID();
    }

    public boolean isInWorld()
    {
        return outsiderEntity.get()!=null;
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
}
