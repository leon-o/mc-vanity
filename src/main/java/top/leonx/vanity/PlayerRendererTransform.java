package top.leonx.vanity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.client.BodyPartRenderer;
import top.leonx.vanity.client.BodyPartRendererRegistry;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.tileentity.VanityBedTileEntity;
import top.leonx.vanity.util.Color;
import top.leonx.vanity.util.ColorUtil;
import top.leonx.vanity.bodypart.BodyPartStack;

public class PlayerRendererTransform {
    public static void fakeSetModelVisibilities(PlayerModel<AbstractClientPlayerEntity> model)
    {
        model.setVisible(false);
    }

    public static void renderItem(MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn, boolean isLeftArm, PlayerModel<AbstractClientPlayerEntity> model)
    {
        BodyPartCapability.BodyPartData bodyPartData = playerIn.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        for (BodyPartStack itemStack : bodyPartData.getItemStacksList()) {
            BodyPartRenderer renderer = BodyPartRendererRegistry.getRenderer(itemStack.getItem());
            if(renderer==null) continue;
            Color color = ColorUtil.splitColor(itemStack.getColor(),true);
            renderer.renderFirstPerson(matrixStackIn,bufferIn,combinedLightIn,playerIn,isLeftArm,model,color);
        }
    }

    public static void renderLeftArm(PlayerRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn)
    {
        renderItem(matrixStackIn,bufferIn,combinedLightIn,playerIn,true,renderer.getEntityModel());
    }
    public static void renderRightArm(PlayerRenderer renderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, AbstractClientPlayerEntity playerIn)
    {
        renderItem(matrixStackIn,bufferIn,combinedLightIn,playerIn,false,renderer.getEntityModel());
    }
//    private static void a(MethodNode node)
//    {
//        FieldInsnNode entityModel = new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/renderer/entity/PlayerRenderer", "entityModel", "Lnet/minecraft/client/renderer/entity/model/EntityModel;");
//        TypeInsnNode  typeInsnNode = new TypeInsnNode(Opcodes.CHECKCAST, "net/minecraft/client/renderer/entity/model/PlayerModel");
//        node.localVariables.clear();
//        InsnList list=new InsnList();
//
//    }

}
