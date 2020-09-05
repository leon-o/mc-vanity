package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHairModel extends Model {

    public AbstractHairModel() {
        super(RenderType::getEntityCutoutNoCull);
        renderers=new ArrayList<>();
        originRotates=new ArrayList<>();
        this.textureHeight=64;
        this.textureWidth=64;
    }
    private final List<ModelRenderer> renderers;
    private final List<Vector3f> originRotates;
    @Override
    public void accept(ModelRenderer modelRenderer) {
        renderers.add(modelRenderer);
        originRotates.add(new Vector3f());
    }

    public void render (LivingEntity livingEntity, float partialTicks, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red,
                        float green, float blue, float alpha)
    {
        for (int i = 0; i < renderers.size(); i++) {
            ModelRenderer renderer = renderers.get(i);
            originRotates.get(i).set(renderer.rotateAngleX,renderer.rotateAngleY,renderer.rotateAngleZ);
        }
        applyPhysic(livingEntity,partialTicks);
        render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);

        for (int i = 0; i < originRotates.size(); i++) {
            Vector3f      f = originRotates.get(i);
            ModelRenderer renderer = renderers.get(i);
            renderer.rotateAngleX=f.getX();
            renderer.rotateAngleY=f.getY();
            renderer.rotateAngleZ=f.getZ();
        }
    }

    public void applyPhysic(LivingEntity livingEntity,float partialTicks) {
        float gravityRotateX= -(float)Math.toRadians(livingEntity.rotationPitch);
        float rotateAngleX=0;
        float rotateAngleZ=0;
        float rotateAngleY=0;
        if(livingEntity instanceof PlayerEntity)
        {
            PlayerEntity player=(PlayerEntity)livingEntity;
            double d0 = MathHelper.lerp(partialTicks, player.prevChasingPosX, player.chasingPosX) - MathHelper.lerp(partialTicks, player.prevPosX, player.getPosX());
            double d1 = MathHelper.lerp(partialTicks, player.prevChasingPosY, player.chasingPosY) - MathHelper.lerp(partialTicks, player.prevPosY, player.getPosY());
            double d2 = MathHelper.lerp(partialTicks, player.prevChasingPosZ, player.chasingPosZ) - MathHelper.lerp(partialTicks, player.prevPosZ, player.getPosZ());
            float f = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset);
            double d3 = MathHelper.sin(f * ((float)Math.PI / 180F));
            double d4 = -MathHelper.cos(f * ((float)Math.PI / 180F));
            float f1 = (float)d1 * 10.0F;
            f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
            float f2 = (float)(d0 * d3 + d2 * d4) * 100.0F;
            f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
            float f3 = (float)(d0 * d4 - d2 * d3) * 100.0F;
            f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
            if (f2 < 0.0F) {
                f2 = 0.0F;
            }

            float f4 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
            f1 = f1 + MathHelper.sin(MathHelper.lerp(partialTicks, player.prevDistanceWalkedModified, player.distanceWalkedModified) * 6.0F) * 32.0F * f4;
            if (player.isCrouching()) {
                f1 += 25.0F;
            }
            rotateAngleX=(float)Math.toRadians(6.0F + f2 / 2.0F + f1);
            rotateAngleZ=(float)Math.toRadians(f3 / 2.0F);
            rotateAngleY=(float)Math.toRadians(f3 / 2.0F);
        }
        applyPhysic(gravityRotateX,rotateAngleX,rotateAngleY,rotateAngleZ);
    }

    public abstract void applyPhysic(float gravityAngleX,float rotateDeltaX,float rotateDeltaY,float rotateDeltaZ);
}
