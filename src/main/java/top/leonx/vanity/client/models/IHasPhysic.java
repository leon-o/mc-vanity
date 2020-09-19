package top.leonx.vanity.client.models;

import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public interface IHasPhysic {

    default void applyPhysic(LivingEntity livingEntity, float partialTicks){
        if(livingEntity instanceof PlayerEntity) {
            PlayerEntity player         = (PlayerEntity) livingEntity;
            double       deltaX         = MathHelper.lerp(partialTicks, player.prevChasingPosX, player.chasingPosX) - MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX());
            double       deltaY         = MathHelper.lerp(partialTicks, player.prevChasingPosY, player.chasingPosY) - MathHelper.lerp(partialTicks, player.prevPosY, player.getPosY());
            double       deltaZ         = MathHelper.lerp(partialTicks, player.prevChasingPosZ, player.chasingPosZ) - MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ());
            Vec3d        deltaDis       =new Vec3d(deltaX,deltaY,deltaZ);
            float        deltaYawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset);
            float        deltaYaw = MathHelper.lerp(partialTicks,player.prevRotationYaw,player.rotationYaw);
            float        deltaPitch =  MathHelper.lerp(partialTicks,player.rotationPitch , player.prevRotationPitch);
            applyPhysic(livingEntity,partialTicks,deltaDis,deltaYawOffset,deltaYaw,deltaPitch);
        }
    }

    void applyPhysic(LivingEntity livingEntity,float partialTicks,Vec3d deltaDis,float deltaYawOffset,float deltaYaw,float deltaPitch);
}
