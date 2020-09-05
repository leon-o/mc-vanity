package top.leonx.vanity.client.renderer.bodypart;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.ArmorLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.util.Color;

import java.util.Map;


public class SkinBodyPartRenderer extends BodyPartRenderer {

    static SkinModel slimModel=new SkinModel(0,true);
    static SkinModel normModel=new SkinModel(0,false);

    final static ArmorLayer armorLayer=new BipedArmorLayer(null,slimModel,normModel);
    ResourceLocation skinLocation;
    public SkinBodyPartRenderer(ResourceLocation skinLocation)
    {
        this.skinLocation=skinLocation;
    }


    public static class SkinModel extends BipedModel<LivingEntity>
    {
        private ModelRenderer chestRoot;
        private ModelRenderer chestBottom;
        private ModelRenderer chestTop;

        private ModelRenderer armorChestRoot;
        private ModelRenderer armorChestBottom;
        private ModelRenderer armorChestTop;

        public SkinModel(float modelSize, boolean slim) {
            super(modelSize,0,64,64);

            bipedHeadwear.showModel=false;
            if (slim) {

                float chestHeight=4.375f;

                chestRoot = new ModelRenderer(this);
                chestRoot.setRotationPoint(0.0F, chestHeight, -1.25F);
                chestRoot.setTextureOffset(18, 21).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 2.0F, 2.0F, 0.0F, false);

                chestBottom = new ModelRenderer(this);
                chestBottom.setRotationPoint(0.0F, -0.25F, 0.0F);
                chestRoot.addChild(chestBottom);
                setRotationAngle(chestBottom, 1.0472F, 0.0F, 0.0F);
                chestBottom.setTextureOffset(18, 22).addBox(-4.0F, -1.1071F, -2.0825F, 8.0F, 2.0F, 2.0F, 0.0F, false);

                chestTop = new ModelRenderer(this);
                chestTop.setRotationPoint(0.0F, -0.5F, 0.25F);
                chestRoot.addChild(chestTop);
                setRotationAngle(chestTop, -0.6109F, 0.0F, 0.0F);
                chestTop.setTextureOffset(18, 19).addBox(-4.0F, -1.119F, -2.1299F, 8.0F, 2.0F, 2.0F, 0.0F, false);

                armorChestRoot = new ModelRenderer(64,32,18,21);
                armorChestRoot.setRotationPoint(0.0F, chestHeight, -2F);
                armorChestRoot.addBox(-4.0F, -1.0F, -2.0F, 8.0F, 2.0F, 2.0F, 0.4F, 0.0f,0.0f);

                armorChestBottom = new ModelRenderer(64,32,18,22);
                armorChestBottom.setRotationPoint(0.0F, -0.25F, 0.0F);
                armorChestRoot.addChild(armorChestBottom);
                setRotationAngle(armorChestBottom, 1.0472F, 0.0F, 0.0F);
                armorChestBottom.addBox(-4.0F, -1.1071F, -2.0825F, 8.0F, 2.0F, 2.0F, 0.4F, 0.0f,0.0f);

                armorChestTop = new ModelRenderer(64,32,18,19);
                armorChestTop.setRotationPoint(0.0F, -0.5F, 0.25F);
                armorChestRoot.addChild(armorChestTop);
                setRotationAngle(armorChestTop, -0.6109F, 0.0F, 0.0F);
                armorChestTop.addBox(-4.0F, -1.119F, -2.1299F, 8.0F, 2.0F, 2.0F, 0.4F, 0.0f,0.0f);

                this.bipedBody.addChild(chestRoot);
                this.bipedBody.addChild(armorChestRoot);
                this.bipedLeftArm = new ModelRenderer(this, 32, 48);
                this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize+0.01f,modelSize,modelSize+0.01f);
                this.bipedLeftArm.setRotationPoint(5.0F, 2.5F, 0.0F);
                this.bipedRightArm = new ModelRenderer(this, 40, 16);
                this.bipedRightArm.addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, modelSize+0.01f,modelSize,modelSize+0.01f);
                this.bipedRightArm.setRotationPoint(-5.0F, 2.5F, 0.0F);
            } else {
                this.bipedLeftArm = new ModelRenderer(this, 32, 48);
                this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, modelSize+0.01f,modelSize,modelSize+0.01f);
                this.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
            }
        }
        public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
            modelRenderer.rotateAngleX = x;
            modelRenderer.rotateAngleY = y;
            modelRenderer.rotateAngleZ = z;
        }

    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState, Color col, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(entityModel instanceof BipedModel)
        {
            BipedModel<LivingEntity> model = (BipedModel) entityModel;
            ItemStack itemStackFromSlot = livingEntity.getItemStackFromSlot(EquipmentSlotType.CHEST);
            boolean renderArmor = itemStackFromSlot.getItem() instanceof ArmorItem;
            model.setModelAttributes(slimModel);

            slimModel.setLivingAnimations(livingEntity,limbSwing,limbSwingAmount,partialTicks);
            slimModel.setRotationAngles(livingEntity,limbSwing,limbSwingAmount,ageInTicks,netHeadYaw,headPitch);
            slimModel.armorChestRoot.showModel=false;//Temporarily hide



            slimModel.render(matrixStackIn,bufferIn.getBuffer(RenderType.getEntityCutout(skinLocation)),packedLightIn,packedOverlayIn,col.r,col.g,col.b,col.a);

            if(renderArmor)
            {
                matrixStackIn.push();
                slimModel.bipedBody.translateRotate(matrixStackIn);
                slimModel.armorChestRoot.showModel=true;
                ResourceLocation resource = armorLayer.getArmorResource(livingEntity, itemStackFromSlot, EquipmentSlotType.CHEST, null);
                slimModel.armorChestRoot.render(matrixStackIn,bufferIn.getBuffer(RenderType.getEntityCutout(resource)),packedLightIn,packedOverlayIn);
                matrixStackIn.pop();
            }
        }
    }

    @Override
    public void renderFirstPerson(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn, boolean isLeftArm,
                                  PlayerModel<AbstractClientPlayerEntity> modelIn, Color color) {

        ModelRenderer renderModelIn;
        if(isLeftArm)
        {
            slimModel.leftArmPose=modelIn.leftArmPose;
            renderModelIn=slimModel.bipedLeftArm;
        }else {
            slimModel.rightArmPose=modelIn.rightArmPose;
            renderModelIn=slimModel.bipedRightArm;
        }
        slimModel.swingProgress = 0.0F;
        slimModel.isSneak = false;
        slimModel.swimAnimation = 0.0F;
        slimModel.setRotationAngles(playerIn, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        renderModelIn.rotateAngleX = 0.0F;
        renderModelIn.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(playerIn.getLocationSkin())), combinedLightIn, OverlayTexture.NO_OVERLAY,color.r,color.g,color.b,color.a);

    }
}
