package top.leonx.vanity.data;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.nbt.LongNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class NbtIO<T> {
    public static final NbtIO<BlockPos>                 BLOCK_POS_IO               = new NbtIO<>(t -> LongNBT.valueOf(t.toLong()),
                                                                                                 t -> t == null ? BlockPos.fromLong(0) : BlockPos.fromLong(((LongNBT) t).getLong()));
    public static final NbtIO<UUID>                     UUID_IO                    = new NbtIO<>(t -> StringNBT.valueOf(t.toString()),
                                                                                                 t -> t == null ? new UUID(0, 0) : UUID.fromString(t.getString()));
    public static final NbtIO<Optional<UUID>>           OPTIONAL_UUID_IO           = new NbtIO<>(t -> StringNBT.valueOf(t.orElse(new UUID(0, 0)).toString()), t -> {
        if (t == null) return Optional.empty();
        UUID uuid = UUID.fromString(t.getString());
        if (uuid.getMostSignificantBits() == 0 && uuid.getLeastSignificantBits() == 0) uuid = null;
        return Optional.ofNullable(uuid);
    });
    public static final NbtIO<Optional<BlockPos>>       OPTIONAL_BLOCK_POS_IO      = new NbtIO<>(
            t -> t.map(pos -> new LongArrayNBT(new long[]{pos.toLong()})).orElseGet(() -> new LongArrayNBT(new long[0])), t -> {
        if (t == null) return Optional.empty();
        long[] array = ((LongArrayNBT) t).getAsLongArray();
        if (array.length == 0) return Optional.empty();
        else return Optional.of(BlockPos.fromLong(array[0]));
    });
    public static final NbtIO<Optional<ITextComponent>> OPTIONAL_TEXT_COMPONENT_IO = new NbtIO<>(
            t -> t.map(text -> StringNBT.valueOf(ITextComponent.Serializer.toJson(text))).orElse(StringNBT.valueOf("")), t -> {
        if (t == null) return Optional.empty();
        String str = t.getString();
        return str.length() == 0 ? Optional.empty() : Optional.ofNullable(ITextComponent.Serializer.fromJson(str));
    });
    private final       Writer<T>                       writer;
    private final       Reader<T>                       reader;

    public NbtIO(Writer<T> writer, Reader<T> reader) {
        this.writer = writer;
        this.reader = reader;
    }

    public Writer<T> getWriter() {
        return writer;
    }

    public Reader<T> getReader() {
        return reader;
    }

    public interface Writer<T> {
        INBT write(T value);
    }

    public interface Reader<T> {
        T read(@Nullable INBT inbt);
    }
}
