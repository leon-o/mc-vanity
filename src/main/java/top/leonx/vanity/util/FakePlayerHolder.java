package top.leonx.vanity.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.lang.ref.WeakReference;
import java.util.UUID;

public class FakePlayerHolder {
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
            FakePlayerHolder.fakePlayer = new WeakReference<>(fakePlayer);
        }
        else
        {
            fakePlayer.get().world = server;
        }
        return fakePlayer;
    }
}
