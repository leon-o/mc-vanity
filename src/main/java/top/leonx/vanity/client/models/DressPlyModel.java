package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DressPlyModel extends PlyModel implements IHasPhysic {
    List<Face> facesWithPhysic = new ArrayList<>();

    public DressPlyModel(ResourceLocation modelLocation) {
        super(modelLocation);
    }

    @Override
    public void applyPhysic(LivingEntity livingEntity, float partialTicks, Vec3d deltaDis, float deltaYawOffset, float deltaYaw, float deltaPitch) {
        facesWithPhysic.clear();
        float    scaleFac  = Math.max(-0.1f, (float) deltaDis.y * 0.175f);
        Matrix4f trsMatrix = Matrix4f.makeScale(1 + scaleFac, 1, 1 + scaleFac);
        deltaDis = deltaDis.rotateYaw((float) Math.toRadians(livingEntity.rotationYaw));
        trsMatrix.mul(Matrix4f.makeTranslate(/*(float) deltaDis.x*/0, 0.05f * (float) deltaDis.y,
                                                                   -0.2f * (float) deltaDis.z));
        trsMatrix.mul(new Quaternion(Vector3f.XP, (float) deltaDis.z, true));
        for (Face face : faces) {
            Vertex[] verticesNew = new Vertex[face.vertices.length];
            for (int i = 0; i < face.vertices.length; i++) {

                Vertex   vertex   = face.vertices[i];
                Vector4f posOrigin= new Vector4f(vertex.position);
                Vector4f posTrans = new Vector4f(vertex.position);
                float[]  colors   = vertex.colors;


                posTrans.transform(trsMatrix);
                posTrans.set(MathHelper.lerp(colors[0], posOrigin.getX(), posTrans.getX()),
                        MathHelper.lerp(colors[0], posOrigin.getY(), posTrans.getY()),
                        MathHelper.lerp(colors[0], posOrigin.getZ(), posTrans.getZ()), 1);

                verticesNew[i] = new Vertex(new Vector3f(posTrans.getX(), posTrans.getY(), posTrans.getZ()),
                                            vertex.normal, vertex.texCoords, colors);
            }
            facesWithPhysic.add(new Face(verticesNew));
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        if(facesWithPhysic.size()==0)
            super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        else
            renderFaces(facesWithPhysic, matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }
}
