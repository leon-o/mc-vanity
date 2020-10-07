package top.leonx.vanity.client.renderer.bodypart;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.util.Color;

import java.util.Map;
import java.util.function.Function;

public class EyeBodyPartRender extends BodyPartRenderer {



    public static class EyeModel extends Model
    {
        private final ModelRenderer socketModelRender;
        private final ModelRenderer socketColoredModelRender;
        private final ModelRenderer eyeballModelRender;
        private final ModelRenderer eyeballColoredModelRender;
        Vector3f eyeballTranslate;
        float heightTranslate;
        public void setEyeballTranslate(Vector3f eyeballTranslate) {
            this.eyeballTranslate = eyeballTranslate;
        }

        public void setHeightTranslate(float heightTranslate) {
            this.heightTranslate = heightTranslate;
        }

        public EyeModel(Function<ResourceLocation, RenderType> renderTypeIn) {
            super(renderTypeIn);
            textureHeight=16;
            textureWidth=16;
            socketModelRender =new ModelRenderer(this, 0, 0);
            socketModelRender.setRotationPoint(0, 0, 0);
            socketModelRender.addBox(-4f, -8f, -4.005f, 8f, 8f, 0f, 0.00f,0,0f);

            socketColoredModelRender =new ModelRenderer(this, 0, 8);
            socketColoredModelRender.setRotationPoint(0, 0, 0);
            socketColoredModelRender.addBox(-4f, -8f, -4.008f, 8f, 8f, 0f, 0.00f,0,0f);

            eyeballModelRender = new ModelRenderer(this, 8, 0);
            eyeballModelRender.setRotationPoint(0, 0, 0);
            eyeballModelRender.addBox(-4f, -8f, -4.014f, 8f, 8f, 0f, 0.25f,0,0f);

            eyeballColoredModelRender = new ModelRenderer(this, 8, 8);
            eyeballColoredModelRender.setRotationPoint(0, 0, 0);
            eyeballColoredModelRender.addBox(-4f, -8f, -4.016f, 8f, 8f, 0f, 0.25f,0,0f);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            matrixStackIn.push();
            matrixStackIn.translate(0,heightTranslate,0);
            matrixStackIn.translate(eyeballTranslate.getX()*0.0005,eyeballTranslate.getY()*0.0005,eyeballTranslate.getZ()*0.0005);
            socketModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,1,1,1,1);
            socketColoredModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);

            matrixStackIn.translate(eyeballTranslate.getX()*0.00025,eyeballTranslate.getY()*0.00025,eyeballTranslate.getZ()*0.00025);
            eyeballModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,1,1,1,1);
            eyeballColoredModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
            matrixStackIn.pop();
        }
    }
    private final EyeModel model;
    private ResourceLocation location;
    public EyeBodyPartRender(ResourceLocation textureLocation)
    {
        model=new EyeModel(RenderType::getEntityCutout);
        this.location=textureLocation;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState, Color color, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStackIn.push();

        if(livingEntity.isSleeping()) { //close eyes when sleeping
            matrixStackIn.translate(0,-0.175,0);
            matrixStackIn.scale(1, 0.1f, 1);
            matrixStackIn.translate(0,0.175,0);
        }

        ModelRenderer modelHead = entityModel.getModelHead();
        modelHead.translateRotate(matrixStackIn);
        RenderType renderType = model.getRenderType(location);
        Float      height     = attributes.getOrDefault("eye_height", 0f);

        model.setEyeballTranslate(new Vector3f(-MathHelper.clamp(MathHelper.wrapDegrees(netHeadYaw),-30,30),
                                               MathHelper.clamp(MathHelper.wrapDegrees(headPitch),-30,5),
                                               0));
        model.setHeightTranslate(height);

        model.render(matrixStackIn,bufferIn.getBuffer(renderType),packedLightIn,packedOverlayIn,color.r,color.g,color.b,color.a);
        matrixStackIn.pop();
    }
}
