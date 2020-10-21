package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class BaseHairModel extends AbstractHairModel{
    private final ModelRenderer hairModelRender;
    private final ModelRenderer hairWearModelRender;

    public BaseHairModel() {
        hairModelRender=new ModelRenderer(this,0,0);
        hairModelRender.setRotationPoint(0,0,0);
        hairModelRender.addBox(-4f,-8f,-4f,8f,8f,8f,0.01f);

        hairWearModelRender = new ModelRenderer(this,32,0);
        hairWearModelRender.setRotationPoint(0,0,0);
        hairWearModelRender.addBox(-4f,-8f,-4f,8f,8f,8f,0.52f);
    }

    @Override
    public void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ) {
        //DO NOTHING
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        hairModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        hairWearModelRender.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
    }

    @Override
    public void resetPhysic() {

    }
}
