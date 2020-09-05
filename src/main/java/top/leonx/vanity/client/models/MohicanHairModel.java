package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class MohicanHairModel extends AbstractHairModel {
    private final ModelRenderer bone;
    private final ModelRenderer bone3;
    private final ModelRenderer bone2;

    public MohicanHairModel() {
        textureWidth = 64;
        textureHeight = 64;

        bone = new ModelRenderer(this);
        bone.setRotationPoint(0.0F, -3.75F, 4.0F);
        bone.setTextureOffset(0, 53).addBox(-1.0F, -6.8F, 0.0F, 2.0F, 8.0F, 3.0F, 0.0F, false);

        bone3 = new ModelRenderer(this);
        bone3.setRotationPoint(0.0F, -6.75F, 1.0F);
        bone.addChild(bone3);
        setRotationAngle(bone3, -0.5236F, 0.0F, 0.0F);
        bone3.setTextureOffset(0, 48).addBox(-1.0F, -1F, -1.0F, 2.0F, 2.0F, 3.0F, -0.02F, false);

        bone2 = new ModelRenderer(this);
        bone2.setRotationPoint(0.0F, 0.0F, 0.0F);
        bone.addChild(bone2);
        bone2.setTextureOffset(10, 50).addBox(-1.0F, -8.0F, -9.0F, 2.0F, 4.0F, 10.0F, -0.01F, false);

    }

    @Override
    public void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ) {
        // DO NOTHING
    }


    @Override
    public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone.render(matrixStack, buffer, packedLight, packedOverlay,red,green,blue,alpha);
    }

    public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
