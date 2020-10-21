package top.leonx.vanity.client.models;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;

public class LongHairModel extends AbstractHairModel {
	private final ModelRenderer bone;
	private final ModelRenderer bone2;
	private final ModelRenderer bone3;
	private ModelRenderer[]     Bones;

	private final ModelRenderer front;
	private final ModelRenderer front2;
	private final ModelRenderer front3;
	public LongHairModel() {
		textureWidth = 64;
		textureHeight = 64;
		front = new ModelRenderer(this);
		front.setRotationPoint(0.0F, -6.0F, -4.0F);
		front.setTextureOffset(38, 45).addBox(-5.0F, -1.0F, -1.0F, 10.0F, 3.0F, 3.0F, -0.478F,0.0f,-0.478F);

		front2 = new ModelRenderer(this);
		front2.setRotationPoint(0.0F, 2.0F, -1.0F);
		front.addChild(front2);
		front2.setTextureOffset(38, 51).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 4.0F, 3.0F, -0.478F,0.0f,-0.478F);

		front3 = new ModelRenderer(this);
		front3.setRotationPoint(0.0F, 4.0F, 0.0F);
		front2.addChild(front3);
		front3.setTextureOffset(38, 58).addBox(-5.0F, 0.0F, 0.0F, 10.0F, 3.0F, 3.0F, -0.478F,0.0f,-0.478F);

		bone = new ModelRenderer(this);
		bone.setRotationPoint(0.0F, -5.0F, 4.5F);
		bone.setTextureOffset(0, 43).addBox(-5.0F, -1.0F, -3.0F, 10.0F, 5.0F, 3.0F, -0.478F,0.0F,0.0F);

		bone2 = new ModelRenderer(this);
		bone2.setRotationPoint(0.0F, 3.8F, 0.0F);
		bone.addChild(bone2);
		bone2.setTextureOffset(0, 51).addBox(-5.0F, 0.0F, -2.0F, 10.0F, 5.0F, 2.0F, -0.478F, 0.0F, 0.0F);

		bone3 = new ModelRenderer(this);
		bone3.setRotationPoint(0.0F, 4.8F, 0.0F);
		bone2.addChild(bone3);
		bone3.setTextureOffset(0, 58).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 5.0F, 1.0F, -0.478F, 0.0F, 0.0F);

		Bones=new ModelRenderer[]{
				bone, bone2, bone3,front3
		};
	}

	@Override
	public void render(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		//super.render(matrixStack, buffer, packedLight, packedOverlay,red,green,blue,alpha);
		bone.render(matrixStack, buffer, packedLight, packedOverlay,red,green,blue,alpha);
		front.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}

	@Override
	public void applyPhysic(float gravityAngleX, float rotateDeltaX, float rotateDeltaY, float rotateDeltaZ) {
		float gravityAngleXHalf = gravityAngleX/2;
		float back=(float) Math.max(-Math.PI/12, gravityAngleXHalf);
		float front=(float) Math.min(Math.PI/12, gravityAngleXHalf);
		bone.rotateAngleX  = back;
		bone2.rotateAngleX =back;

		front2.rotateAngleX=front*1.5f;
		front3.rotateAngleX=front*0.5f;

		double deltaRadianX=rotateDeltaX/2;
		double deltaRadianY=rotateDeltaY/2;
		double deltaRadianZ=rotateDeltaZ/2;

		for (ModelRenderer renderer : Bones) {
			renderer.rotateAngleX+=deltaRadianX;
			renderer.rotateAngleZ+=deltaRadianZ;
			renderer.rotateAngleY-=deltaRadianY;
		}
		bone.rotateAngleX=Math.max(0,bone.rotateAngleX);
	}

	@Override
	public void resetPhysic() {
		for (ModelRenderer renderer : Bones) {
			setRotationAngle(renderer,0,0,0);
		}
	}
}