package top.leonx.vanity.client.models;

import com.google.common.collect.Lists;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DressObjModel extends CustomObjModel implements IHasPhysic {
    public Map<String, ModelGroup> physicalParts=new HashMap<>();

    public DressObjModel(ResourceLocation modelLocation, boolean flipV) throws IOException {
        super(modelLocation, flipV);
        parts.forEach((key, value) -> physicalParts.put(key, copyModelGroup(value)));
    }

    @Override
    public void applyPhysic(LivingEntity livingEntity, float partialTicks, Vec3d deltaDis, float deltaYawOffset, float deltaYaw, float deltaPitch) {
        float    scaleFac       = Math.max(-0.1f, (float) deltaDis.y * 0.175f);
        Matrix4f trsMatrix = Matrix4f.makeScale(1+scaleFac, 1, 1+scaleFac);
        deltaDis=deltaDis.rotateYaw((float) Math.toRadians(livingEntity.rotationYaw));
        trsMatrix.mul(Matrix4f.makeTranslate(/*(float) deltaDis.x*/0, 0.05f*(float) deltaDis.y, -0.2f*(float) deltaDis.z));
        trsMatrix.mul(new Quaternion(Vector3f.XP, (float) deltaDis.z, true));
        for (ModelGroup value : physicalParts.values()) {
            for (ModelMesh mesh : value.meshes) {
                for (ModelQuad quad : mesh.quads) {
                    for (int i = 0; i < quad.pos.length; i++) {
                        Vector4f pos        = quad.pos[i];
                        Vector4f col        = quad.colors[i];
                        if(col.getX()==0) continue;
                        Vector4f posTransed=new Vector4f(pos.getX(),pos.getY(),pos.getZ(),pos.getW());
                        posTransed.transform(trsMatrix);
                        pos.set(MathHelper.lerp(col.getX(),pos.getX(),posTransed.getX()),
                                MathHelper.lerp(col.getX(),pos.getY(),posTransed.getY()),
                                MathHelper.lerp(col.getX(),pos.getZ(),posTransed.getZ()),1);
                    }
                }
            }
        }
    }


    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (ModelGroup value : physicalParts.values()) {
            value.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }

        for (String keys : physicalParts.keySet()) {
            ModelGroup originGroup = parts.get(keys);
            ModelGroup modelGroup  = physicalParts.get(keys);
            for (int i = 0; i < originGroup.meshes.size(); i++) {
                ModelMesh o_mesh = originGroup.meshes.get(i);
                ModelMesh p_mesh = modelGroup.meshes.get(i);
                for (int i1 = 0; i1 < o_mesh.quads.size(); i1++) {
                    p_mesh.quads.get(i1).pos = new Vector4f[4];
                    Vector4f[] o_poses = o_mesh.quads.get(i1).pos;
                    for (int j = 0; j <4; j++) {
                        p_mesh.quads.get(i1).pos[j]=new Vector4f(o_poses[j].getX(),o_poses[j].getY(),o_poses[j].getZ(),o_poses[j].getW());
                    }
                }
            }
        }
    }

    private List<ModelMesh> copyMeshes(List<ModelMesh> meshes) {
        List<ModelMesh> newMeshs = Lists.newArrayList();
        for (ModelMesh mesh : meshes) {
            ModelMesh newMesh = new ModelMesh(mesh.mat, mesh.smoothingGroup);
            newMesh.faces.addAll(mesh.faces);
            newMesh.quads = mesh.faces.stream().map(this::makeQuad).collect(Collectors.toList());
            newMeshs.add(newMesh);
        }

        return newMeshs;
    }

    private ModelGroup copyModelGroup(ModelGroup group) {
        ModelGroup newGroup = new ModelGroup(group.name);
        newGroup.meshes = copyMeshes(group.meshes);
        return newGroup;
    }
}
