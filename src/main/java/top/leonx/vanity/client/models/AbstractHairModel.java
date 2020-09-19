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
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHairModel extends Model implements IHasPhysic {

    private final List<ModelRenderer> renderers;
    private final List<Vector3f>      originRotates;
    public AbstractHairModel() {
        super(RenderType::getEntityCutoutNoCull);
        renderers = new ArrayList<>();
        originRotates = new ArrayList<>();
        this.textureHeight = 64;
        this.textureWidth = 64;
    }

    @Override
    public void accept(ModelRenderer modelRenderer) {
        renderers.add(modelRenderer);
        originRotates.add(new Vector3f());
    }

    public void render(LivingEntity livingEntity, float partialTicks, MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (int i = 0; i < renderers.size(); i++) {
            ModelRenderer renderer = renderers.get(i);
            originRotates.get(i).set(renderer.rotateAngleX, renderer.rotateAngleY, renderer.rotateAngleZ);
        }
        applyPhysic(livingEntity, partialTicks);
        render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);

        for (int i = 0; i < originRotates.size(); i++) {
            Vector3f      f        = originRotates.get(i);
            ModelRenderer renderer = renderers.get(i);
            renderer.rotateAngleX = f.getX();
            renderer.rotateAngleY = f.getY();
            renderer.rotateAngleZ = f.getZ();
        }
    }

    public void applyPhysic(LivingEntity livingEntity, float partialTicks, Vec3d deltaDis, float deltaYawOffset, float deltaYaw, float deltaPitch) {
        float        gravityRotateX = -(float) Math.toRadians(livingEntity.rotationPitch);
        float        rotateAngleX;
        float        rotateAngleZ;
        float        rotateAngleY;
        PlayerEntity player;
        if (!(livingEntity instanceof PlayerEntity)) return;
        player = ((PlayerEntity) livingEntity);

        double deltaYawX   = MathHelper.sin(deltaYawOffset * ((float) Math.PI / 180F));
        double deltaYawZ   = -MathHelper.cos(deltaYawOffset * ((float) Math.PI / 180F));
        float  deltaYMul10 = (float) deltaDis.y * 10.0F;
        deltaYMul10 = MathHelper.clamp(deltaYMul10, -6.0F, 32.0F);
        float f2 = (float) (deltaDis.x * deltaYawX + deltaDis.z * deltaYawZ) * 100.0F;
        f2 = MathHelper.clamp(f2, 0.0F, 150.0F);
        float f3 = (float) (deltaDis.x * deltaYawZ - deltaDis.z * deltaYawX) * 100.0F;
        f3 = MathHelper.clamp(f3, -20.0F, 20.0F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        float f4 = MathHelper.lerp(partialTicks, player.prevCameraYaw, player.cameraYaw);
        deltaYMul10 = deltaYMul10 + MathHelper.sin(MathHelper.lerp(partialTicks, player.prevDistanceWalkedModified, player.distanceWalkedModified) * 6.0F) * 32.0F * f4;
        if (player.isCrouching()) {
            deltaYMul10 += 25.0F;
        }
        rotateAngleX = (float) Math.toRadians(6.0F + f2 / 2.0F + deltaYMul10);
        rotateAngleZ = (float) Math.toRadians(f3 / 2.0F);
        rotateAngleY = (float) Math.toRadians(f3 / 2.0F);

        applyPhysic(gravityRotateX, rotateAngleX, rotateAngleY, rotateAngleZ);
    }

    public abstract void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ);
}
