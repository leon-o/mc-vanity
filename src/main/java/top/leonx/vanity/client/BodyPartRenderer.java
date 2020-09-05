package top.leonx.vanity.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.util.Color;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public abstract class BodyPartRenderer {

    public abstract <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState, Color color, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch);

    public void renderFirstPerson(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn,
                                  AbstractClientPlayerEntity playerIn, boolean isLeftArm, PlayerModel<AbstractClientPlayerEntity> model, Color color){}
}
