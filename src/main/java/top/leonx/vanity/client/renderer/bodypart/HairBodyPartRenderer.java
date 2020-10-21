package top.leonx.vanity.client.renderer.bodypart;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.client.models.AbstractHairModel;
import top.leonx.vanity.util.Color;

import java.util.Map;

public class HairBodyPartRenderer extends BodyPartRenderer {

    AbstractHairModel model;
    RenderType renderType;
    public HairBodyPartRenderer(AbstractHairModel model, ResourceLocation texture)
    {
        this.model=model;
        this.renderType=model.getRenderType(texture);
    }

    public AbstractHairModel getModel() {
        return model;
    }

    public RenderType getRenderType() {
        return renderType;
    }


    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState, Color color, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(livingEntity instanceof ZombieEntity && livingEntity.isChild())
            return;
        matrixStackIn.push();
        entityModel.getModelHead().translateRotate(matrixStackIn);
        if(entityModel instanceof VillagerModel)
            matrixStackIn.scale(1,20f/16f,1);

        AbstractHairModel baseHairModel = getModel();
        if(baseHairModel!=null) {
            baseHairModel.applyPhysic(livingEntity, partialTicks);
            baseHairModel.render(matrixStackIn, bufferIn.getBuffer(getRenderType()), packedLightIn, OverlayTexture.NO_OVERLAY, color.r,color.g,color.b,color.a);
            baseHairModel.resetPhysic();
        }

        matrixStackIn.pop();
    }
}
