package top.leonx.vanity.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import top.leonx.vanity.init.ModCapabilityTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CharacterStateCapabilityProvider implements ICapabilitySerializable<INBT> {

    LazyOptional<CharacterState> characterStateLazyOptional =LazyOptional.of(CharacterState::new);
    private final Capability.IStorage<CharacterState> storage = ModCapabilityTypes.CHARACTER_STATE.getStorage();
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap== ModCapabilityTypes.CHARACTER_STATE)
            return characterStateLazyOptional.cast();
        else return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return storage.writeNBT(ModCapabilityTypes.CHARACTER_STATE, characterStateLazyOptional.orElse(CharacterState.EMPTY), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        storage.readNBT(ModCapabilityTypes.CHARACTER_STATE, characterStateLazyOptional.orElse(CharacterState.EMPTY), null, nbt);
    }
}
