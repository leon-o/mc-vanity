package top.leonx.vanity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.leonx.vanity.init.ModCapabilityTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BodyPartCapabilityProvider implements ICapabilitySerializable<INBT> {

    LazyOptional<BodyPartCapability.BodyPartData> hairCapabilityLazyOptional =LazyOptional.of(BodyPartCapability.BodyPartData::new);
    private final Capability.IStorage<BodyPartCapability.BodyPartData> storage = ModCapabilityTypes.BODY_PART.getStorage();
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap== ModCapabilityTypes.BODY_PART)
            return hairCapabilityLazyOptional.cast();
        else return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return storage.writeNBT(ModCapabilityTypes.BODY_PART, hairCapabilityLazyOptional.orElse(ModCapabilityTypes.BODY_PART.getDefaultInstance()), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        storage.readNBT(ModCapabilityTypes.BODY_PART, hairCapabilityLazyOptional.orElse(ModCapabilityTypes.BODY_PART.getDefaultInstance()), null, nbt);
    }
}
