package top.leonx.vanity.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class CharacterStateCapabality {
    public static class Storage implements Capability.IStorage<CharacterState>
    {

        @Nullable
        @Override
        public INBT writeNBT(Capability<CharacterState> capability, CharacterState instance, Direction side) {
            CompoundNBT compoundNBT=new CompoundNBT();
            compoundNBT.put("root",instance.getRoot());
            compoundNBT.putBoolean("need_init",instance.isNeedInit());
            return instance.getRoot();
        }

        @Override
        public void readNBT(Capability<CharacterState> capability, CharacterState instance, Direction side, INBT nbt) {
            if(nbt.getId()==10)
            {
                CompoundNBT compoundNBT = (CompoundNBT) nbt;
                instance.setRoot(compoundNBT.getCompound("root"));
                instance.setNeedInit(compoundNBT.getBoolean("need_init"));
            }
        }
    }

    public static class CharacterStateFactory implements Callable<CharacterState> {
        @Override
        public CharacterState call() {
            return new CharacterState();
        }
    }
}
