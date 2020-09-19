/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package top.leonx.vanity.client.models;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import joptsimple.internal.Strings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.Vector4f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.model.obj.LineReader;
import net.minecraftforge.client.model.obj.MaterialLibrary;
import net.minecraftforge.client.model.obj.OBJLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CustomObjModel extends Model
{
    private static final Vector4f COLOR_WHITE    = new Vector4f(1, 1, 1, 1);
    private static final Vec2f[]  DEFAULT_COORDS = {
            new Vec2f(0, 0),
            new Vec2f(0, 1),
            new Vec2f(1, 1),
            new Vec2f(1, 0),
    };

    protected final Map<String, ModelGroup> parts = Maps.newHashMap();

    protected final List<Vector3f> positions = Lists.newArrayList();
    protected final List<Vec2f> texCoords = Lists.newArrayList();
    protected final List<Vector3f> normals = Lists.newArrayList();
    protected final List<Vector4f> colors = Lists.newArrayList();

    //public final boolean detectCullableFaces;
    //public final boolean diffuseLighting;
    public final boolean flipV;
    //public final boolean ambientToFullbright;

    public final ResourceLocation modelLocation;

    //@Nullable
    //public final String materialLibraryOverrideLocation;


    public CustomObjModel(ResourceLocation modelLocation,boolean flipV) throws IOException
    {
        super(RenderType::getEntityCutoutNoCull);
        this.modelLocation = modelLocation;

        LineReader reader=new LineReader(Minecraft.getInstance().getResourceManager().getResource(modelLocation));

        //this.detectCullableFaces = settings.detectCullableFaces;
        //this.diffuseLighting = settings.diffuseLighting;
        this.flipV = flipV;
        //this.ambientToFullbright = settings.ambientToFullbright;
        //this.materialLibraryOverrideLocation = settings.materialLibraryOverrideLocation;

        // for relative references to material libraries
        String modelDomain = modelLocation.getNamespace();
        String modelPath = modelLocation.getPath();
        int lastSlash = modelPath.lastIndexOf('/');
        if (lastSlash >= 0)
            modelPath = modelPath.substring(0,lastSlash+1); // include the '/'
        else
            modelPath = "";

        MaterialLibrary          mtllib     = MaterialLibrary.EMPTY;
        MaterialLibrary.Material currentMat = null;
        String currentSmoothingGroup = null;
        ModelGroup currentGroup = null;
        ModelObject currentObject = null;
        ModelMesh currentMesh = null;

        boolean objAboveGroup = false;

//        if (materialLibraryOverrideLocation != null)
//        {
//            String lib = materialLibraryOverrideLocation;
//            if (lib.contains(":"))
//                mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
//            else
//                mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));
//        }

        String[] line;
        while((line = reader.readAndSplitLine(true)) != null)
        {
            switch(line[0])
            {
                case "mtllib": // Loads material library
                {
//                    if (materialLibraryOverrideLocation != null)
//                        break;
                    try {
                        String lib = line[1];
                        if (lib.contains(":"))
                            mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(lib));
                        else
                            mtllib = OBJLoader.INSTANCE.loadMaterialLibrary(new ResourceLocation(modelDomain, modelPath + lib));

                    }catch (Exception ignore){}
                    break;
                }

                case "usemtl": // Sets the current material (starts new mesh)
                {
                    String mat = Strings.join(Arrays.copyOfRange(line, 1, line.length), " ");
                    MaterialLibrary.Material newMat=null;
                    try{
                        newMat = mtllib.getMaterial(mat);
                    }catch (NoSuchElementException ignored){

                    }

                    if (!Objects.equals(newMat, currentMat))
                    {
                        currentMat = newMat;
                        if (currentMesh != null && currentMesh.mat == null && currentMesh.faces.size() == 0)
                        {
                            currentMesh.mat = currentMat;
                        }
                        else
                        {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "v": // Vertex
                    positions.add(parseVector4To3(line));
                    break;
                case "vt": // Vertex texcoord
                    texCoords.add(parseVector2(line));
                    break;
                case "vn": // Vertex normal
                    normals.add(parseVector3(line));
                    break;
                case "vc": // Vertex color (non-standard)
                    colors.add(parseVector4(line));
                    break;

                case "f": // Face
                {
                    if (currentMesh == null)
                    {
                        currentMesh = new ModelMesh(currentMat, currentSmoothingGroup);
                        if (currentObject != null)
                        {
                            currentObject.meshes.add(currentMesh);
                        }
                        else
                        {
                            if (currentGroup == null)
                            {
                                currentGroup = new ModelGroup("");
                                parts.put("", currentGroup);
                            }
                            currentGroup.meshes.add(currentMesh);
                        }
                    }

                    int[][] vertices = new int[line.length-1][];
                    for(int i=0;i<vertices.length;i++)
                    {
                        String vertexData = line[i+1];
                        String[] vertexParts = vertexData.split("/");
                        int[] vertex = Arrays.stream(vertexParts).mapToInt(num -> Strings.isNullOrEmpty(num) ? 0 : Integer.parseInt(num)).toArray();
                        if (vertex[0] < 0) vertex[0] = positions.size() + vertex[0];
                        else vertex[0]--;
                        if (vertex.length > 1)
                        {
                            if (vertex[1] < 0) vertex[1] = texCoords.size() + vertex[1];
                            else vertex[1]--;
                            if (vertex.length > 2)
                            {
                                if (vertex[2] < 0) vertex[2] = normals.size() + vertex[2];
                                else vertex[2]--;
                                if (vertex.length > 3)
                                {
                                    if (vertex[3] < 0) vertex[3] = colors.size() + vertex[3];
                                    else vertex[3]--;
                                }
                            }
                        }
                        vertices[i] = vertex;
                    }

                    currentMesh.faces.add(vertices);

                    break;
                }

                case "s": // Smoothing group (starts new mesh)
                {
                    String smoothingGroup = "off".equals(line[1]) ? null : line[1];
                    if (!Objects.equals(currentSmoothingGroup, smoothingGroup))
                    {
                        currentSmoothingGroup = smoothingGroup;
                        if (currentMesh != null && currentMesh.smoothingGroup == null && currentMesh.faces.size() == 0)
                        {
                            currentMesh.smoothingGroup = currentSmoothingGroup;
                        }
                        else
                        {
                            // Start new mesh
                            currentMesh = null;
                        }
                    }
                    break;
                }

                case "g":
                {
                    String name = line[1];
                    if (objAboveGroup)
                    {
                        currentObject = new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    }
                    else
                    {
                        currentGroup = new ModelGroup(name);
                        parts.put(name, currentGroup);
                        currentObject = null;
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }

                case "o":
                {
                    String name = line[1];
                    if (objAboveGroup || currentGroup == null)
                    {
                        objAboveGroup = true;

                        currentGroup = new ModelGroup(name);
                        parts.put(name, currentGroup);
                        currentObject = null;
                    }
                    else
                    {
                        currentObject = new ModelObject(currentGroup.name() + "/" + name);
                        currentGroup.parts.put(name, currentObject);
                    }
                    // Start new mesh
                    currentMesh = null;
                    break;
                }
            }
        }


        for (ModelGroup value : parts.values()) {
            for (ModelMesh mesh : value.meshes) {
                mesh.quads=mesh.faces.stream().map(this::makeQuad).collect(Collectors.toList());
            }
        }
    }

    public static Vector3f parseVector4To3(String[] line)
    {
        switch (line.length) {
            case 1: return new Vector3f(0,0,0);
            case 2: return new Vector3f(Float.parseFloat(line[1]), 0, 0);
            case 3: return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0);
            case 4: return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
            default:
            {
                Vector4f vec4 = parseVector4(line);
                return new Vector3f(
                        vec4.getX() / vec4.getW(),
                        vec4.getY() / vec4.getW(),
                        vec4.getZ() / vec4.getW()
                );
            }
        }
    }

    public static Vec2f parseVector2(String[] line)
    {
        switch (line.length) {
            case 1: return new Vec2f(0,0);
            case 2: return new Vec2f(Float.parseFloat(line[1]), 0);
            default: return new Vec2f(Float.parseFloat(line[1]), Float.parseFloat(line[2]));
        }
    }

    public static Vector3f parseVector3(String[] line)
    {
        switch (line.length) {
            case 1: return new Vector3f(0,0,0);
            case 2: return new Vector3f(Float.parseFloat(line[1]), 0, 0);
            case 3: return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0);
            default: return new Vector3f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]));
        }
    }

    public static Vector4f parseVector4(String[] line)
    {
        switch (line.length) {
            case 1: return new Vector4f(0,0,0,1);
            case 2: return new Vector4f(Float.parseFloat(line[1]), 0, 0,1);
            case 3: return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), 0,1);
            case 4: return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]),1);
            default: return new Vector4f(Float.parseFloat(line[1]), Float.parseFloat(line[2]), Float.parseFloat(line[3]), Float.parseFloat(line[4]));
        }
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (ModelGroup value : parts.values()) {
            value.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha);
        }
    }

    protected ModelQuad makeQuad(int[][] indices)
    {
        boolean needsNormalRecalculation = false;
        for (int[] ints : indices)
        {
            needsNormalRecalculation |= ints.length < 3;
        }
        Vector3f faceNormal = new Vector3f(0,0,0);
        if (needsNormalRecalculation) {
            Vector3f a = positions.get(indices[0][0]);
            Vector3f ab = positions.get(indices[1][0]);
            Vector3f ac = positions.get(indices[2][0]);
            Vector3f abs = ab.copy();
            abs.sub(a);
            Vector3f acs = ac.copy();
            acs.sub(a);
            abs.cross(acs);
            abs.normalize();
            faceNormal = abs;
        }

        Vector4f[] pos = new Vector4f[4];
        Vector3f[] norm = new Vector3f[4];
        Vec2f[] texs = new Vec2f[4];
        Vector4f[] cols = new Vector4f[4];

        //BakedQuadBuilder builder = new BakedQuadBuilder(texture);

        //builder.setQuadTint(tintIndex);

        //Vec2f uv2 = new Vec2f(0, 0);


        for(int i=0;i<4;i++)
        {
            int[] index = indices[Math.min(i,indices.length-1)];
            Vector3f pos0 = positions.get(index[0]);
            Vector4f position = new Vector4f(pos0);
            Vec2f texCoord = index.length >= 2 && texCoords.size() > 0 ? texCoords.get(index[1]) : DEFAULT_COORDS[i];
            Vector3f normal = !needsNormalRecalculation && index.length >= 3 && normals.size() > 0 ? normals.get(index[2]) : faceNormal;

            Vector4f color = index.length >= 4 && colors.size() > 0 ? colors.get(index[3]) : COLOR_WHITE;

            pos[i] = position;
            norm[i] = normal;
            cols[i]=color;
            texs[i]=texCoord;
        }


        return new ModelQuad(pos, faceNormal, norm, texs,cols);
    }

    public static class ModelObject
    {
        public final String name;

        List<ModelMesh> meshes = Lists.newArrayList();

        ModelObject(String name)
        {
            this.name = name;
        }

        public String name()
        {
            return name;
        }

        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            MatrixStack.Entry matrix = matrixStackIn.getLast();
            for (ModelMesh mesh : meshes) {
                for (ModelQuad quad : mesh.quads) {
                    for (int i = 0; i < 4; i++) {
                        Vector4f pos4f  = quad.pos[i];
                        Vector4f pos = new Vector4f(pos4f.getX(),pos4f.getY(),pos4f.getZ(),pos4f.getW()); //copy
                        pos.transform(matrix.getMatrix());

                        Vector3f norm=quad.vecNormals[i].copy();
                        norm.transform(matrix.getNormal());
                        bufferIn.addVertex(pos.getX(),pos.getY(),pos.getZ(),red,green,blue,alpha,quad.texCoords[i].x,quad.texCoords[i].y,packedOverlayIn,packedLightIn,norm.getX(),norm.getY(),norm.getZ());
                    }
                }
            }
        }
    }

    public static class ModelGroup extends ModelObject
    {
        final Map<String, ModelObject> parts = Maps.newHashMap();

        ModelGroup(String name)
        {
            super(name);
        }

        @Override
        public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
            super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            parts.values().forEach(t->t.render(matrixStackIn,bufferIn,packedLightIn,packedOverlayIn,red,green,blue,alpha));
        }
    }

    protected static class ModelMesh
    {
        @Nullable
        public MaterialLibrary.Material mat;

        public String smoothingGroup;
        public final List<int[][]> faces = Lists.newArrayList();
        public List<ModelQuad> quads=Lists.newArrayList();
        public ModelMesh(@Nullable MaterialLibrary.Material currentMat, @Nullable String currentSmoothingGroup)
        {
            this.mat = currentMat;
            this.smoothingGroup = currentSmoothingGroup;
        }
    }
    public static class ModelQuad{
        public Vector4f[] pos;
        public Vector3f faceNormal;
        public Vector3f[] vecNormals;
        public Vec2f[] texCoords;
        public Vector4f[] colors;

        public ModelQuad(Vector4f[] pos, Vector3f faceNormal, Vector3f[] vecNormals, Vec2f[] texCoords,Vector4f[] colors) {
            this.pos = pos;
            this.faceNormal = faceNormal;
            this.vecNormals = vecNormals;
            this.texCoords = texCoords;
            this.colors=colors;
        }
    }
    public static class ModelSettings
    {
        @Nonnull
        public final ResourceLocation modelLocation;
        public final boolean detectCullableFaces;
        public final boolean diffuseLighting;
        public final boolean flipV;
        public final boolean ambientToFullbright;
        @Nullable
        public final String materialLibraryOverrideLocation;

        public ModelSettings(@Nonnull ResourceLocation modelLocation, boolean detectCullableFaces, boolean diffuseLighting, boolean flipV, boolean ambientToFullbright,
                             @Nullable String materialLibraryOverrideLocation)
        {
            this.modelLocation = modelLocation;
            this.detectCullableFaces = detectCullableFaces;
            this.diffuseLighting = diffuseLighting;
            this.flipV = flipV;
            this.ambientToFullbright = ambientToFullbright;
            this.materialLibraryOverrideLocation = materialLibraryOverrideLocation;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ModelSettings that = (ModelSettings) o;
            return equals(that);
        }

        public boolean equals(@Nonnull ModelSettings that)
        {
            return detectCullableFaces == that.detectCullableFaces &&
                    diffuseLighting == that.diffuseLighting &&
                    flipV == that.flipV &&
                    ambientToFullbright == that.ambientToFullbright &&
                    modelLocation.equals(that.modelLocation) &&
                    Objects.equals(materialLibraryOverrideLocation, that.materialLibraryOverrideLocation);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(modelLocation, detectCullableFaces, diffuseLighting, flipV, ambientToFullbright, materialLibraryOverrideLocation);
        }
    }
}