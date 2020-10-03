package top.leonx.vanity.event;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

public class GamePlayEventSubscriber {
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvent {
        @SubscribeEvent
        public static void onPlayerRightClick(PlayerInteractEvent.RightClickBlock event) {
            if(event.getWorld().isRemote) return;
            World        world  = event.getWorld();
            BlockPos     pos    = event.getPos();
            PlayerEntity player = event.getPlayer();
            BlockState   state  = world.getBlockState(pos);
            if (state.getBlock().isBed(state, world, pos, player)
                    && !state.getBlock().isBedFoot(state,world,pos)
                    && player.isSneaking()) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), ((VanityBedTileEntity) world.getTileEntity(pos)), pos);
                event.setCanceled(true);
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvent {

    }
}
