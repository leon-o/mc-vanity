package top.leonx.vanity.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.client.models.IHasPhysic;
import top.leonx.vanity.client.models.SkirtModel;
import top.leonx.vanity.util.Color;

public class SkirtClothItem extends AbstractClothItem {
    static final ResourceLocation MODEL_LOCATION   = new ResourceLocation(VanityMod.MOD_ID,
                                                                          "models/bodypart/dress.ply");
    static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(VanityMod.MOD_ID,
                                                                          "textures/bodypart/dress" + "/dress_debug.png");

    @Override
    public RenderType getRenderType() {
        return RenderType.getEntityCutout(getTextureLocation());
    }

    @Override
    public Model createModel(LivingEntity entity) {
        return new SkirtModel(MODEL_LOCATION);
    }

    ResourceLocation getTextureLocation() {
        return TEXTURE_LOCATION;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    protected void render(String identifier, MatrixStack matrixStackIn, IRenderTypeBuffer renderTypeBuffer, int light, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        EntityRenderer<? super LivingEntity> renderer = Minecraft.getInstance().getRenderManager().getRenderer(
                livingEntity);
        if (!(renderer instanceof LivingRenderer)) return;

        LivingRenderer<LivingEntity, EntityModel<LivingEntity>> livingRenderer = (LivingRenderer) renderer;
        EntityModel<LivingEntity>                               model          = livingRenderer.getEntityModel();
        if (!(model instanceof BipedModel)) return;
        BipedModel bipedModel = (BipedModel) model;

        matrixStackIn.push();
        bipedModel.bipedBody.translateRotate(matrixStackIn);

        matrixStackIn.rotate(new Quaternion(Vector3f.ZP, 180, true));
        matrixStackIn.translate(0, -0.525, 0);

        Color color = getColor(identifier, livingEntity);

        Model clothModel = getClothModel(livingEntity);

        ((IHasPhysic) clothModel).applyPhysic(livingEntity, partialTicks);
        clothModel.render(matrixStackIn, renderTypeBuffer.getBuffer(getRenderType()), light, OverlayTexture.NO_OVERLAY,
                          color.r, color.g, color.b, color.a);
        ((IHasPhysic) clothModel).resetPhysic();

        matrixStackIn.pop();
    }
}
