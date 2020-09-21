package top.leonx.vanity.client.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.VillagerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieEntity;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.client.BodyPartRendererRegistry;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.util.Color;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.bodypart.BodyPart;
import top.leonx.vanity.bodypart.BodyPartStack;

import java.util.List;

public class BodyPartLayer<T extends LivingEntity, M extends EntityModel<T> & IHasHead> extends LayerRenderer<T, M> {
    public BodyPartLayer(IEntityRenderer<T, M> render) {
        super(render);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, LivingEntity livingEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        M entityModel = getEntityModel();

        if(livingEntity instanceof ZombieEntity && livingEntity.isChild())
            return;

        if(entityModel instanceof VillagerModel)
            matrixStackIn.scale(1,20f/16f,1);


        BodyPartCapability.BodyPartData bodyPartData = livingEntity.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        CharacterState                  stateData    =livingEntity.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
        List<BodyPartStack>             stacksList   = bodyPartData.getItemStacksList();
        if(stacksList==null) return;
        for (BodyPartStack stack : stacksList) {
            Color color = ColorUtil.splitColor(stack.getColor(),true);

            BodyPart vanityItem = stack.getItem();
            if(vanityItem !=null)
            {
                BodyPartRenderer renderer = BodyPartRendererRegistry.getRenderer(vanityItem);
                if(renderer!=null)
                    renderer.render(livingEntity, entityModel, stack.getAdjustableAttributes(), stateData, color, matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);
            }
        }



        //matrixStackIn.pop();
    }
}
