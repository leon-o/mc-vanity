package top.leonx.vanity.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.leonx.vanity.VanityMod;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class VanityPacketHandler {
    public static SimpleChannel CHANNEL;

    public static void Init()
    {
        CHANNEL = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(VanityMod.MOD_ID, "main"),
                () -> "1",
                "1"::equals,
                "1"::equals
        );
    }
    static int idCount=0;
    public static <T> void registerMessage(Class<T> type,
    BiConsumer<T, PacketBuffer> encoder, Function<PacketBuffer,
            T> decoder, BiConsumer<T,Supplier<NetworkEvent.Context>> handler)
    {
       CHANNEL.registerMessage(idCount++,type,encoder,decoder,handler);
    }
}
