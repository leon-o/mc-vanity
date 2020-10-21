package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DoublePonytailModel extends AbstractHairModel {
    private final ModelRenderer band;
    private final ModelRenderer bone;
    private final ModelRenderer bone2;
    private final ModelRenderer bone3;
    private final ModelRenderer bone7;
    private final ModelRenderer band2;
    private final ModelRenderer bone4;
    private final ModelRenderer bone5;
    private final ModelRenderer bone6;
    private final ModelRenderer bone8;

    public DoublePonytailModel() {
        textureWidth = 64;
        textureHeight = 64;

        band = new ModelRenderer(this);
        band.setRotationPoint(-2.0F, -3.0F, 4.0F);
        band.setTextureOffset(12, 57).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, 0.0F, 0.0F);
        band.addChild(bone);
        setRotationAngle(bone, 0.829F, 0.0F, 0.2618F);
        bone.setTextureOffset(0, 57).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(0.0F, 1.5F, 0.5F);
        bone.addChild(bone2);
        setRotationAngle(bone2, -0.3054F, 0.0F, 0.0F);
        bone2.setTextureOffset(0, 59).addBox(-1.0F, -0.1144F, -1.0698F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(0.0F, 3.4884F, -0.5756F);
        bone2.addChild(bone3);
        setRotationAngle(bone3, -0.2182F, 0.0F, 0.0F);
        bone3.setTextureOffset(8, 59).addBox(-1.0F, -0.9782F, -0.751F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        bone7 = new ModelRenderer(this);
        bone7.setRotationPoint(0.0F, 2.0F, 0.0F);
        bone3.addChild(bone7);
        bone7.setTextureOffset(8, 57).addBox(-0.517F, -0.1507F, -0.2378F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        band2 = new ModelRenderer(this);
        band2.setRotationPoint(2.0F, -3.0F, 4.0F);
        band2.setTextureOffset(12, 57).addBox(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);

        bone4 = new ModelRenderer(this);
        bone4.setRotationPoint(0.0F, 0.0F, 0.0F);
        band2.addChild(bone4);
        setRotationAngle(bone4, 0.829F, 0.0F, -0.2618F);
        bone4.setTextureOffset(0, 57).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F, false);

        bone5 = new ModelRenderer(this);
        bone5.setRotationPoint(0.0F, 1.5F, 0.5F);
        bone4.addChild(bone5);
        setRotationAngle(bone5, -0.3054F, 0.0F, 0.0F);
        bone5.setTextureOffset(0, 59).addBox(-1.0F, -0.1144F, -1.0698F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        bone6 = new ModelRenderer(this);
        bone6.setRotationPoint(0.0F, 3.4884F, -0.5756F);
        bone5.addChild(bone6);
        setRotationAngle(bone6, -0.2182F, 0.0F, 0.0F);
        bone6.setTextureOffset(8, 59).addBox(-1.0F, -0.9782F, -0.751F, 2.0F, 3.0F, 2.0F, 0.0F, false);

        bone8 = new ModelRenderer(this);
        bone8.setRotationPoint(0.0F, 2.0F, 0.0F);
        bone6.addChild(bone8);
        bone8.setTextureOffset(8, 57).addBox(-0.517F, -0.1507F, -0.2378F, 1.0F, 1.0F, 1.0F, 0.0F, false);

    }


    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        //super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        band.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        band2.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }

    @Override
    public void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ) {
        band.rotateAngleX= Math.max(gravityAngleX,0);

        band.rotateAngleX+=rotateDeltaX;
        //band.rotateAngleZ+=rotateDeltaZ;
        //band.rotateAngleY-=rotateDeltaY; 会鬼畜

        band2.rotateAngleX= Math.max(gravityAngleX,0);

        band2.rotateAngleX+=rotateDeltaX;
        //band2.rotateAngleZ+=rotateDeltaZ;
        //band2.rotateAngleY-=rotateDeltaY;
    }

    @Override
    public void resetPhysic() {
        setRotationAngle(band,0,0,0);
        setRotationAngle(band2,0,0,0);
    }
}
