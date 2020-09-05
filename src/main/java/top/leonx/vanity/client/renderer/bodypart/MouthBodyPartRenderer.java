package top.leonx.vanity.client.renderer.bodypart;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.IHasHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.util.Color;

import java.util.Map;

public class MouthBodyPartRenderer extends BodyPartRenderer {
    public ResourceLocation mouthLocation;

    public MouthBodyPartRenderer(ResourceLocation mouthLocation) {
        this.mouthLocation = mouthLocation;
    }
    @Override
    public <T extends LivingEntity, M extends EntityModel<T> & IHasHead> void render(LivingEntity livingEntity, M entityModel, Map<String, Float> attributes, CharacterState characterState, Color color, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, int packedOverlayIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {

        CharacterState.MOOD mood = characterState.getMOOD();
        float uSize=0.5f,vSize=0.25f;
        float vStart=0;
        float uStart=0;
        switch (mood) {
            case NORMAL:
                break;
            case HAPPY:
                uStart=0.5f;
                break;
            case ANGRY:
                vStart=0.25f;
                break;
            case SAD:
                uStart=0.5f;
                vStart=0.25f;
                break;
            case SURPRISED:
                vStart=0.5f;
                break;
        }

        float mouthHeight = attributes.getOrDefault("mouth_height", (float) 0);
        float mouthSize=attributes.getOrDefault("mouth_size",(float)1);

        matrixStackIn.push();
        entityModel.getModelHead().translateRotate(matrixStackIn);
        matrixStackIn.translate(0,-mouthHeight-0.025-0.03125,0);
        matrixStackIn.scale(mouthSize / 16f, mouthSize / 16f, 1 / 16f);

        MatrixStack.Entry last   = matrixStackIn.getLast();
        IVertexBuilder    buffer = bufferIn.getBuffer(RenderType.getEntityTranslucentCull(mouthLocation));

        Vector3f normal = new Vector3f(0, 0, -1f);
        Vector4f v1     = new Vector4f(1, -0.5f, -4.02f, 1.0F);
        Vector4f v2     = new Vector4f(-1, -0.5f, -4.02f, 1.0F);
        Vector4f v3     = new Vector4f(-1, 0.5f, -4.02f, 1.0F);
        Vector4f v4     = new Vector4f(1, 0.5f, -4.02f, 1.0F);

        v1.transform(last.getMatrix());
        v2.transform(last.getMatrix());
        v3.transform(last.getMatrix());
        v4.transform(last.getMatrix());

        normal.transform(last.getNormal());

        buffer.addVertex(v1.getX(), v1.getY(), v1.getZ(), color.r, color.g, color.b, color.a, uStart+uSize, vStart, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
        buffer.addVertex(v2.getX(), v2.getY(), v2.getZ(), color.r, color.g, color.b, color.a, uStart, vStart, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
        buffer.addVertex(v3.getX(), v3.getY(), v3.getZ(), color.r, color.g, color.b, color.a, uStart, vStart+vSize, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
        buffer.addVertex(v4.getX(), v4.getY(), v4.getZ(), color.r, color.g, color.b, color.a, uStart+uSize, vStart+vSize, packedOverlayIn, packedLightIn, normal.getX(), normal.getY(), normal.getZ());

        matrixStackIn.pop();
    }
}
