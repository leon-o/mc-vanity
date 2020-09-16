package top.leonx.vanity.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.event.RegistryEvent;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class PlayerSimulator {
    private static GameProfile gameProfile;
    private static WeakReference<ServerPlayerEntity> fakePlayer;

    public static void register()
    {
        gameProfile = new GameProfile(UUID.randomUUID(), "[OutsiderSim]");
        fakePlayer = new WeakReference<>(null);
    }

    public static WeakReference<ServerPlayerEntity> getFakePlayer(ServerWorld server)
    {
        if (fakePlayer.get() == null)
        {
            FakePlayer fakePlayer = FakePlayerFactory.get(server, gameProfile);
            fakePlayer.connection=new DummyServerPlayNetHandler(fakePlayer);
            PlayerSimulator.fakePlayer = new WeakReference<>(fakePlayer);
        }
        else
        {
            fakePlayer.get().world = server;
        }
        return fakePlayer;
    }
}
