package top.leonx.vanity.client.renderer.bodypart;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.client.models.DressObjModel;
import top.leonx.vanity.util.Color;

import java.util.Map;

public class DressBodyPartRenderer extends BodyPartRenderer {
    DressObjModel    dressModel;
    ResourceLocation texturesLocation;
    public DressBodyPartRenderer(DressObjModel dressModel,ResourceLocation texturesLocation) {
        this.dressModel = dressModel;
        this.texturesLocation=texturesLocation;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState,
                                                                                      Color color, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entityModel instanceof BipedModel<?>)
        {
            IVertexBuilder buffer = bufferIn.getBuffer(dressModel.getRenderType(texturesLocation));
            matrixStackIn.push();

            BipedModel<?> bipedModel=(BipedModel<?>) entityModel;
            bipedModel.bipedBody.translateRotate(matrixStackIn);

            matrixStackIn.rotate(new Quaternion(Vector3f.ZP,180,true));
            matrixStackIn.translate(0,-0.525,0);

            dressModel.applyPhysic(livingEntity,partialTicks);
            dressModel.render(matrixStackIn,buffer,packedLightIn,packedOverlayIn,color.r,color.g,color.b,color.a);

            matrixStackIn.pop();
        }
    }
}
