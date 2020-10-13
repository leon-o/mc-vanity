package top.leonx.vanity.client.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraftforge.client.model.obj.LineReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlyModel extends Model {
    private final ResourceLocation modelLocation;
    public Vertex[] vertices=new Vertex[0];
    public Face[] faces= new Face[0];

    public PlyModel(ResourceLocation modelLocation) throws IOException
    {
        super(RenderType::getEntityCutoutNoCull);
        this.modelLocation = modelLocation;
        readPlyFile();
    }

    private void readPlyFile() throws IOException
    {
        LineReader reader =new LineReader(Minecraft.getInstance().getResourceManager().getResource(modelLocation));

        String[] head = reader.readAndSplitLine(true);
        if (head==null || !head[0].equals("ply")) {
            throw new IOException("Not .ply file");
        }
        String[] format = reader.readAndSplitLine(true);
        if (format==null || !format[0].equals("format") || !format[1].equals("ascii")) {
            throw new IOException("Format must be ascii");
        }


        List<PropertyType> vertexFormat=new ArrayList<>();

        //read head
        String[] headLine;
        while((headLine = reader.readAndSplitLine(true)) != null)
        {
            boolean end=false;
            switch (headLine[0])
            {
                case "comment":
                    break;
                case "element":
                {
                    switch (headLine[1])
                    {
                        case "vertex":
                        {
                            int count = Integer.parseInt(headLine[2]);
                            vertices=new Vertex[count];
                            break;
                        }
                        case "face":
                        {
                            int count = Integer.parseInt(headLine[2]);
                            faces=new Face[count];
                            break;
                        }
                    }
                    break;
                }
                case "property":
                {
                    if(!headLine[1].equals("list"))
                        vertexFormat.add(PropertyType.prase(headLine[2]));
                    break;
                }
                case "end_header":
                {
                    end=true;
                    break;
                }
            }
            if(end)
                break;
        }

        for (int i = 0; i < vertices.length; i++) {
            String[] vertexLine = reader.readAndSplitLine(true);
            if(vertexLine==null) break;
            vertices[i]= parseToVertex(vertexLine, vertexFormat);
        }

        for (int i = 0; i < faces.length; i++) {
            String[] faceLine = reader.readAndSplitLine(true);
            if(faceLine==null) break;
            faces[i]=makeFace(faceLine,vertices);
        }
    }

    public Vertex parseToVertex(String[] lines, List<PropertyType> vertexFormat)
    {
        float x=0,y=0,z=0,nx=0,ny=0,nz=0,u=0,v=0;
        short r=0,g=0,b=0,a=0;
        for (int i = 0; i < lines.length; i++) {
            switch (vertexFormat.get(i))
            {

                case X:
                    x=Float.parseFloat(lines[i]);
                    break;
                case Y:
                    y=Float.parseFloat(lines[i]);
                    break;
                case Z:
                    z=Float.parseFloat(lines[i]);
                    break;
                case NX:
                    nx=Float.parseFloat(lines[i]);
                    break;
                case NY:
                    ny=Float.parseFloat(lines[i]);
                    break;
                case NZ:
                    nz=Float.parseFloat(lines[i]);
                    break;
                case U:
                    u=Float.parseFloat(lines[i]);
                    break;
                case V:
                    v=Float.parseFloat(lines[i]);
                    break;
                case RED:
                    r=Short.parseShort(lines[i]);
                    break;
                case GREEN:
                    g=Short.parseShort(lines[i]);
                    break;
                case BLUE:
                    b=Short.parseShort(lines[i]);
                    break;
                case ALPHA:
                    a=Short.parseShort(lines[i]);
                    break;
            }
        }

        return new Vertex(new Vector3f(x,y,z),new Vector3f(nx,ny,nz),new Vec2f(u,v),new short[]{r,g,b,a});
    }

    public Face makeFace(String[] faceLine,Vertex[] vertices)
    {
        int vertexCount=Integer.parseInt(faceLine[0]);
        Vertex[] verticesSelected=new Vertex[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            int vertexIndex=Integer.parseInt(faceLine[i+1]);
            verticesSelected[i]=vertices[vertexIndex];
        }
        return new Face(verticesSelected);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {

    }

    public static class Face
    {
        Vertex[] vertices;
        Vector3f faceNormal=new Vector3f(0,0,0);

        public Face(Vertex[] vertices) {
            this.vertices = vertices;

            for (int i = 0; i < vertices.length-2; i++) {

                Vector3f inversePointOrigin= vertices[i+1].position.copy();
                inversePointOrigin.mul(-1,-1,-1);

                Vector3f edgeA = vertices[i].position.copy();
                edgeA.add(inversePointOrigin);

                Vector3f edgeB = vertices[i+2].position.copy();
                edgeB.add(inversePointOrigin);

                edgeA.cross(edgeB); // compute normal vector by cross two edge

                faceNormal.add(edgeA);
            }

            faceNormal.normalize();
        }
    }

    public static class Vertex
    {
        public Vector3f position;
        public Vector3f normal;
        public Vec2f  texCoords;
        public short[] colors;

        public Vertex(Vector3f position, Vector3f normal, Vec2f texCoords, short[] colors) {
            this.position = position;
            this.normal = normal;
            this.texCoords = texCoords;
            this.colors = colors;
        }
    }
    enum PropertyType{
        X,Y,Z,NX,NY,NZ,U,V,RED,GREEN,BLUE,ALPHA;

        public static PropertyType prase(String type)
        {
            switch (type)
            {
                case "x":
                    return X;
                case "y":
                    return Y;
                case "z":
                    return Z;
                case "nx":
                    return NX;
                case "ny":
                    return NY;
                case "nz":
                    return NZ;
                case "s":
                    return U;
                case "t":
                    return V;
                case "red":
                    return RED;
                case "green":
                    return GREEN;
                case "blue":
                    return BLUE;
                default:
                    return ALPHA;
            }
        }
    }
}
