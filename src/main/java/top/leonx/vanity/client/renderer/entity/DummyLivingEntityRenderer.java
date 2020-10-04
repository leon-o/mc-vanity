package top.leonx.vanity.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.entity.DummyLivingEntity;

public class DummyLivingEntityRenderer extends EntityRenderer<DummyLivingEntity> {


    public DummyLivingEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public ResourceLocation getEntityTexture(DummyLivingEntity entity) {
        return null;
    }

    @Override
    public boolean shouldRender(DummyLivingEntity livingEntityIn, ClippingHelperImpl camera, double camX, double camY, double camZ) {
        return false;
    }

    @Override
    public void render(DummyLivingEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        // do nothing.
    }

    @Override
    protected void renderName(DummyLivingEntity entityIn, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        // do nothing.
    }
}
