package top.leonx.vanity.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import top.leonx.vanity.data.IncorporealDataManager;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * 虚体，生造的概念，和实体对应
 * 实体从世界上被移除后，以虚体的形式存在
 */
public class OutsiderIncorporeal extends CapabilityProvider<OutsiderIncorporeal> implements IOutsider {
    IncorporealDataManager dataManager = new IncorporealDataManager(this);
    UUID                   realUniqueId;
    boolean isChild;
    private WeakReference<OutsiderEntity> outsiderEntity;
    private CompoundNBT                   offlineOutsiderComponent = new CompoundNBT();
    private boolean                       dirty                    = false;

    public OutsiderIncorporeal(UUID uuid) {
        super(OutsiderIncorporeal.class);
        this.gatherCapabilities();
        this.realUniqueId = uuid;
        registerDataParameter();
    }

    public CompoundNBT getComponent() {
        return offlineOutsiderComponent;
    }

    public void setEntityComponent(CompoundNBT entityComponent) {
        this.offlineOutsiderComponent= entityComponent;
        readComponent(entityComponent);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void bindTo(OutsiderEntity entity) {
        outsiderEntity = new WeakReference<>(entity);


        isChild = entity.isChild();

        List<EntityDataManager.DataEntry<?>> allEntries = entity.getDataManager().getAll();
        if (allEntries != null) for (EntityDataManager.DataEntry dataEntry : allEntries) {
            if (dataManager.hasKey(dataEntry.getKey())) dataManager.setWithNotify(dataEntry.getKey(), dataEntry.getValue());
            else dataManager.register(dataEntry.getKey(), dataEntry.getValue());
        }
//        if(offlineOutsiderComponent ==null)
//            setEntityComponent(entity.writeWithoutTypeId(new CompoundNBT()));
    }
    public boolean isInWorld() {
        return outsiderEntity.get() != null;
    }

    public OutsiderEntity getOutsiderEntity() {
        return outsiderEntity.get();
    }

    public IncorporealDataManager getDataManager() {
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
        if(isInWorld())
            getOutsiderEntity().notifyDataManagerChangeFromIncorporeal(key, dataManager.get(key));
    }

    public <T> void notifyDataManagerChangeFromEntity(DataParameter<T> key,T value) {
        dataManager.setWithNotify(key,value);
    }


    public void readComponent(CompoundNBT compound)
    {
/*        if(compound.contains("CustomName",8))
            customName=ITextComponent.Serializer.fromJson(compound.getString("CustomName"));*/
        CompoundNBT capsNbt= compound.getCompound("ForgeCaps");
        if(capsNbt.size()>0)
            deserializeCaps(capsNbt);
    }

    ITextComponent emptyCustomName =new StringTextComponent("Nobody");
    public ITextComponent getCustomName()
    {
        return getDataManager().get(Entity.CUSTOM_NAME).orElse(emptyCustomName); //Helpless but only like this
    }

    public void writeComponent(CompoundNBT compoundNBT) {
        CompoundNBT serializeCaps = serializeCaps();
        if(serializeCaps!=null)
        {
            CompoundNBT capsNbt = compoundNBT.getCompound("ForgeCaps");
            capsNbt.merge(serializeCaps);
        }
    }
}
