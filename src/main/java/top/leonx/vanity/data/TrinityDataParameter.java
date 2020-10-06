package top.leonx.vanity.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.IDataSerializer;

public class TrinityDataParameter<T> extends DataParameter<T> {
    private final NbtIO<T>  nbtIO;
    private final String keyName;
    public TrinityDataParameter(DataParameter<T> dataParameter,String name,NbtIO<T> nbtIO)
    {
        this(dataParameter.getId(),dataParameter.getSerializer(),name,nbtIO);
    }
    public TrinityDataParameter(int idIn, IDataSerializer<T> serializerIn,String name,NbtIO<T> nbtIO) {
        super(idIn, serializerIn);
        this.keyName=name;
        this.nbtIO=nbtIO;
    }

    public void write(CompoundNBT compound,T value)
    {
        INBT nbt = nbtIO.getWriter().write(value);
        compound.put(keyName,nbt);
    }
    public T read(CompoundNBT compound)
    {
        INBT inbt = compound.get(keyName);
        return nbtIO.getReader().read(inbt);
    }
}
