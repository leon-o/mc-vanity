package top.leonx.vanity.util;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.lighting.WorldLightManager;
import top.leonx.vanity.bodypart.BodyPartStack;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.client.BodyPartRendererRegistry;
import top.leonx.vanity.entity.DummyLivingEntity;
import top.leonx.vanity.entity.OfflineOutsider;
import top.leonx.vanity.init.ModCapabilityTypes;

import java.util.List;

public class RenderUtil {
    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,float yawDelta, LivingEntity livingEntity) {
        float f = (float)Math.atan(mouseX / 40.0F);
        float f1 = (float)Math.atan(mouseY / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion  = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion cameraQuatern = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(Vector3f.YP.rotationDegrees(yawDelta));
        quaternion.multiply(cameraQuatern);
        matrixstack.rotate(quaternion);
        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        cameraQuatern.conjugate();
        renderManager.setCameraOrientation(cameraQuatern);
        renderManager.setRenderShadow(false);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        renderManager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0, 1.0F, matrixstack, buffer, 0xf00000);
        buffer.finish();
        renderManager.setRenderShadow(true);
        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }
    static final BipedModel<DummyLivingEntity> bipedModel = new BipedModel<>(0f);
    public static void drawOfflineOutsiderOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, float yawDelta, OfflineOutsider outsider)
    {
        DummyLivingEntity livingEntity = DummyLivingEntityHolder.getDummyLivingEntity(Minecraft.getInstance().world).get();
        List<BodyPartStack> bodyPartStacks = outsider.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY).getItemStacksList();
        bipedModel.isChild=outsider.isChild();

        float f = (float)Math.atan(mouseX / 40.0F);
        float f1 = (float)Math.atan(mouseY / 40.0F);
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float)posX, (float)posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float)scale, (float)scale, (float)scale);
        Quaternion quaternion  = Vector3f.YP.rotationDegrees(yawDelta);
        Quaternion cameraQuatern = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(cameraQuatern);
        matrixstack.rotate(quaternion);
        matrixstack.translate(0.0D, -1.501F, 0.0D);
        //float yawOffset = livingEntity.renderYawOffset;
        float yawBody = livingEntity.rotationYaw;
        float pitch = livingEntity.rotationPitch;
        //float prevYawHead = livingEntity.prevRotationYawHead;
        //float yawHead = livingEntity.rotationYawHead;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        /*livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;*/
        EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
        cameraQuatern.conjugate();
        renderManager.setCameraOrientation(cameraQuatern);
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();

        RenderSystem.disableLighting();
        for (BodyPartStack stack : bodyPartStacks) {
            BodyPartRenderer renderer = BodyPartRendererRegistry.getRenderer(stack.getItem());
            renderer.render(livingEntity, bipedModel, stack.getAdjustableAttributes(), CharacterState.EMPTY,ColorUtil.splitColor(stack.getColor(),true),matrixstack , buffer, 0xF00000,
                            OverlayTexture.NO_OVERLAY ,0,0,0,0,0,pitch);
        }
        RenderSystem.enableLighting();
        buffer.finish();
        livingEntity.rotationYaw = yawBody;
/*        livingEntity.renderYawOffset = yawOffset;
        livingEntity.rotationYaw = yawBody;
        livingEntity.rotationPitch = pitch;
        livingEntity.prevRotationYawHead = prevYawHead;
        livingEntity.rotationYawHead = yawHead;*/
        RenderSystem.popMatrix();
    }
}
