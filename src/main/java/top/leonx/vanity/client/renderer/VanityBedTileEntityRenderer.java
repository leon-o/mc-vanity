package top.leonx.vanity.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.DualBrightnessCallback;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import top.leonx.vanity.init.ModTileEntityTypes;
import top.leonx.vanity.tileentity.VanityBedTileEntity;

public class VanityBedTileEntityRenderer extends TileEntityRenderer<VanityBedTileEntity> {
    private final ModelRenderer   headModel;
    private final ModelRenderer   footModel;
    private final ModelRenderer[] cornerModels = new ModelRenderer[4];

    public VanityBedTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        this.headModel = new ModelRenderer(64, 64, 0, 0);
        this.headModel.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
        this.footModel = new ModelRenderer(64, 64, 0, 22);
        this.footModel.addBox(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 6.0F, 0.0F);
        this.cornerModels[0] = new ModelRenderer(64, 64, 50, 0);
        this.cornerModels[1] = new ModelRenderer(64, 64, 50, 6);
        this.cornerModels[2] = new ModelRenderer(64, 64, 50, 12);
        this.cornerModels[3] = new ModelRenderer(64, 64, 50, 18);
        this.cornerModels[0].addBox(0.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
        this.cornerModels[1].addBox(0.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
        this.cornerModels[2].addBox(-16.0F, 6.0F, -16.0F, 3.0F, 3.0F, 3.0F);
        this.cornerModels[3].addBox(-16.0F, 6.0F, 0.0F, 3.0F, 3.0F, 3.0F);
        this.cornerModels[0].rotateAngleX = ((float) Math.PI / 2F);
        this.cornerModels[1].rotateAngleX = ((float) Math.PI / 2F);
        this.cornerModels[2].rotateAngleX = ((float) Math.PI / 2F);
        this.cornerModels[3].rotateAngleX = ((float) Math.PI / 2F);
        this.cornerModels[0].rotateAngleZ = 0.0F;
        this.cornerModels[1].rotateAngleZ = ((float) Math.PI / 2F);
        this.cornerModels[2].rotateAngleZ = ((float) Math.PI * 1.5F);
        this.cornerModels[3].rotateAngleZ = (float) Math.PI;
    }

    @Override
    public void render(VanityBedTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        Material material = Atlases.BED_TEXTURES[tileEntityIn.getColor().getId()];
        World    world    = tileEntityIn.getWorld();
        if (world != null) {
            BlockState blockstate = tileEntityIn.getBlockState();
            TileEntityMerger.ICallbackWrapper<? extends VanityBedTileEntity> icallbackwrapper = TileEntityMerger.func_226924_a_(ModTileEntityTypes.VANITY_BED.get(), BedBlock::func_226863_i_,
                                                                                                                                BedBlock::func_226862_h_,
                                                                                                                                ChestBlock.FACING, blockstate, world, tileEntityIn.getPos(),
                                                                                                                                (a, b) -> false);
            int i = icallbackwrapper.apply(new DualBrightnessCallback<>()).get(combinedLightIn);
            this.renderInternal(matrixStackIn, bufferIn, blockstate.get(BedBlock.PART) == BedPart.HEAD, blockstate.get(BedBlock.HORIZONTAL_FACING), material, i, combinedOverlayIn, false);
        } else {
            this.renderInternal(matrixStackIn, bufferIn, true, Direction.SOUTH, material, combinedLightIn, combinedOverlayIn, false);
            this.renderInternal(matrixStackIn, bufferIn, false, Direction.SOUTH, material, combinedLightIn, combinedOverlayIn, true);
        }
    }

    private void renderInternal(MatrixStack matrixStack, IRenderTypeBuffer p_228847_2_, boolean isHead, Direction p_228847_4_, Material p_228847_5_, int p_228847_6_, int p_228847_7_, boolean p_228847_8_) {
        this.headModel.showModel = isHead;
        this.footModel.showModel = !isHead;
        this.cornerModels[0].showModel = !isHead;
        this.cornerModels[1].showModel = isHead;
        this.cornerModels[2].showModel = !isHead;
        this.cornerModels[3].showModel = isHead;
        matrixStack.push();
        matrixStack.translate(0.0D, 0.5625D, p_228847_8_ ? -1.0D : 0.0D);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(90.0F));
        matrixStack.translate(0.5D, 0.5D, 0.5D);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(180.0F + p_228847_4_.getHorizontalAngle()));
        matrixStack.translate(-0.5D, -0.5D, -0.5D);
        IVertexBuilder ivertexbuilder = p_228847_5_.getBuffer(p_228847_2_, RenderType::getEntitySolid);
        this.headModel.render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        this.footModel.render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        this.cornerModels[0].render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        this.cornerModels[1].render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        this.cornerModels[2].render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        this.cornerModels[3].render(matrixStack, ivertexbuilder, p_228847_6_, p_228847_7_);
        matrixStack.pop();
    }
}
