package top.leonx.vanity.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import top.leonx.vanity.client.layer.BodyPartLayer;
import top.leonx.vanity.entity.OutsiderEntity;

public class OutsiderRenderer extends LivingRenderer<OutsiderEntity, BipedModel<OutsiderEntity>> {
    boolean isFemale;

    public OutsiderRenderer(EntityRendererManager rendererManager) {
        super(rendererManager, new BipedModel<>(0), 0.5f);
        this.entityModel.setVisible(false);
        addLayer(new BodyPartLayer<>(this));
        addLayer(new HeldItemLayer<>(this));
        addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
    }

    @Override
    public ResourceLocation getEntityTexture(OutsiderEntity entity) {
        return DefaultPlayerSkin.getDefaultSkin(entity.getUniqueID());
    }

    public Vec3d getRenderOffset(OutsiderEntity entityIn, float partialTicks) {
        return entityIn.isCrouching() ? new Vec3d(0.0D, -0.125D, 0.0D) : super.getRenderOffset(entityIn, partialTicks);
    }

    public boolean isFemale() {
        return isFemale;
    }

    public void setFemale(boolean female) {
        isFemale = female;

    }

    public void render(OutsiderEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        this.setModelVisibilities(entityIn);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

//    protected void preRenderCallback(AbstractClientPlayerEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
//        float f = 0.9375F;
//        matrixStackIn.scale(0.9375F, 0.9375F, 0.9375F);
//    }

    protected void applyRotations(OutsiderEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        float f = entityLiving.getSwimAnimation(partialTicks);
        if (entityLiving.isElytraFlying()) {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f1 = (float) entityLiving.getTicksElytraFlying() + partialTicks;
            float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!entityLiving.isSpinAttacking()) {
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f2 * (-90.0F - entityLiving.rotationPitch)));
            }

            Vec3d  vec3d  = entityLiving.getLook(partialTicks);
            Vec3d  vec3d1 = entityLiving.getMotion();
            double d0     = Entity.horizontalMag(vec3d1);
            double d1     = Entity.horizontalMag(vec3d);
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                matrixStackIn.rotate(Vector3f.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
            float f3 = entityLiving.isInWater() ? -90.0F - entityLiving.rotationPitch : -90.0F;
            float f4 = MathHelper.lerp(f, 0.0F, f3);
            matrixStackIn.rotate(Vector3f.XP.rotationDegrees(f4));
            if (entityLiving.isActualySwimming()) {
                matrixStackIn.translate(0.0D, -1.0D, 0.3F);
            }
        } else {
            super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        }

    }

    private BipedModel.ArmPose getArmPose(OutsiderEntity playerIn, ItemStack itemStackMain, ItemStack itemStackOff, Hand handIn) {
        BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
        ItemStack          itemstack          = handIn == Hand.MAIN_HAND ? itemStackMain : itemStackOff;
        if (!itemstack.isEmpty()) {
            bipedmodel$armpose = BipedModel.ArmPose.ITEM;
            if (playerIn.getItemInUseCount() > 0) {
                UseAction useaction = itemstack.getUseAction();
                if (useaction == UseAction.BLOCK) {
                    bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
                } else if (useaction == UseAction.BOW) {
                    bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
                } else if (useaction == UseAction.SPEAR) {
                    bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
                } else if (useaction == UseAction.CROSSBOW && handIn == playerIn.getActiveHand()) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean flag3 = itemStackMain.getItem() == Items.CROSSBOW;
                boolean flag  = CrossbowItem.isCharged(itemStackMain);
                boolean flag1 = itemStackOff.getItem() == Items.CROSSBOW;
                boolean flag2 = CrossbowItem.isCharged(itemStackOff);
                if (flag3 && flag) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }

                if (flag1 && flag2 && itemStackMain.getItem().getUseAction(itemStackMain) == UseAction.NONE) {
                    bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }

        return bipedmodel$armpose;
    }

    @Override
    protected void renderName(OutsiderEntity entityIn, String displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.renderName(entityIn, displayNameIn, matrixStackIn, bufferIn, packedLightIn);
    }

    private void setModelVisibilities(OutsiderEntity outsiderEntity) {
        BipedModel<OutsiderEntity> model = this.getEntityModel();
        model.setVisible(false);
        ItemStack itemstackMainhand  = outsiderEntity.getHeldItemMainhand();
        ItemStack itemstackOffhand = outsiderEntity.getHeldItemOffhand();

        model.isSneak = outsiderEntity.isCrouching();
        BipedModel.ArmPose bipedmodelArmpose  = this.getArmPose(outsiderEntity, itemstackMainhand, itemstackOffhand, Hand.MAIN_HAND);
        BipedModel.ArmPose bipedmodelArmposeOff = this.getArmPose(outsiderEntity, itemstackMainhand, itemstackOffhand, Hand.OFF_HAND);
        if (outsiderEntity.getPrimaryHand() == HandSide.RIGHT) {
            model.rightArmPose = bipedmodelArmpose;
            model.leftArmPose = bipedmodelArmposeOff;
        } else {
            model.rightArmPose = bipedmodelArmposeOff;
            model.leftArmPose = bipedmodelArmpose;
        }

    }
}
