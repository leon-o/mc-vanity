package top.leonx.vanity.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import top.leonx.vanity.bodypart.BodyPartStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BodyPartCapability {
    public static class BodyPartData
    {
        public final static BodyPartData EMPTY    =new BodyPartData();
        private             boolean      needInit =true;
        private final       List<BodyPartStack> itemStacksList =new ArrayList<>();

        public List<BodyPartStack> getItemStacksList() {
            return itemStacksList;
        }

        public boolean isNeedInit() {
            return needInit;
        }

        public void setNeedInit(boolean needInit) {
            this.needInit = needInit;
        }
    }
    public static class Storage implements Capability.IStorage<BodyPartData>
    {

        @Nullable
        @Override
        public INBT writeNBT(Capability<BodyPartData> capability, BodyPartData instance, Direction side) {
            CompoundNBT nbt=new CompoundNBT();
            ListNBT itemList=new ListNBT();

            for (BodyPartStack t : instance.itemStacksList) {
                CompoundNBT itemNbt=t.createNBT();
                if(itemNbt!=null)
                    itemList.add(itemNbt);
            }
            nbt.put("vanity_items",itemList);
            nbt.putBoolean("need_init",instance.needInit);
            return nbt;
        }

        @Override
        public void readNBT(Capability<BodyPartData> capability, BodyPartData instance, Direction side, INBT nbt) {
            if(nbt.getId()==10)
            {
                instance.itemStacksList.clear();
                CompoundNBT rootNbt=(CompoundNBT)nbt;

                ListNBT     itemNbtList       = rootNbt.getList("vanity_items", 10);
                for (INBT inbt : itemNbtList) {
                    CompoundNBT   stackNbt =(CompoundNBT)inbt;
                    BodyPartStack bodyPartStack  = BodyPartStack.createFromNBT(stackNbt);
                    if(bodyPartStack!=null)
                        instance.getItemStacksList().add(bodyPartStack);
                }
                instance.needInit=rootNbt.getBoolean("need_init");
            }
        }
    }

    public static class BodyPartDataFactory implements Callable<BodyPartData> {
        @Override
        public BodyPartData call() {
            return new BodyPartData();
        }
    }
}
