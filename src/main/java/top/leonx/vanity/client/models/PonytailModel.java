package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class PonytailModel extends AbstractHairModel {
    private final ModelRenderer band;
    private final ModelRenderer ponytail;
    private final ModelRenderer ponytailTip;

    public PonytailModel() {
        band = new ModelRenderer(this);
        band.setRotationPoint(0.0F, -5.5F, 4.0F);
        band.setTextureOffset(12, 60).addBox(-1.0F, -1.0F, 0.2F, 2.0F, 2.0F, 2.0F, 0.0F, false);

        ponytail = new ModelRenderer(this);
        ponytail.setRotationPoint(0.0F, 0.0F, 1.0F);
        band.addChild(ponytail);
        setRotationAngle(ponytail, 0.0873F, 0.0F, 0.0F);
        ponytail.setTextureOffset(0, 50).addBox(-2.0F, -0.2F, 0.0F, 4.0F, 12.0F, 2.0F, 0.0F, false);

        ponytailTip = new ModelRenderer(this);
        ponytailTip.setRotationPoint(0.0F, 12.0F, 1.0F);
        ponytail.addChild(ponytailTip);
        setRotationAngle(ponytailTip, 0.0873F, 0.0F, 0.0F);
        ponytailTip.setTextureOffset(20, 62).addBox(-1.5F, -0.2F, -0.5F, 3.0F, 1.0F, 1.0F, 0.0F, false);
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
        setRotationAngle(band,0,0,0);
    }

    @Override
    public void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ) {
        band.rotateAngleX= Math.max(gravityAngleX,0);

        band.rotateAngleX+=rotateDeltaX;
        band.rotateAngleZ+=rotateDeltaZ;
        band.rotateAngleY-=rotateDeltaY;

        band.rotateAngleX= (float) Math.min(Math.PI/2, band.rotateAngleX);
    }
}
