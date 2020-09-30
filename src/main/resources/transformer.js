var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
var Opcodes = Java.type('org.objectweb.asm.Opcodes');
var Label = Java.type('org.objectweb.asm.Label');
var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
var LabelNode=Java.type('org.objectweb.asm.tree.LabelNode');
var JumpInsnNode=Java.type('org.objectweb.asm.tree.JumpInsnNode');
var InsnNode=Java.type('org.objectweb.asm.tree.InsnNode');
var TypeInsnNode=Java.type('org.objectweb.asm.tree.TypeInsnNode');
var FieldInsnNode=Java.type('org.objectweb.asm.tree.FieldInsnNode');
var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

function transformSetModelVisibilities(method)
{
    var insnList=new InsnList();
    var methodNode=new MethodInsnNode(Opcodes.INVOKESTATIC,"top/leonx/vanity/PlayerRendererTransform","fakeSetModelVisibilities","(Lnet/minecraft/client/renderer/entity/model/PlayerModel;)V",false);
    insnList.add(new VarInsnNode(Opcodes.ALOAD,2));
    insnList.add(methodNode);

    var iterator = method.instructions.iterator();
    while (iterator.hasNext())
    {
        var insnNode=iterator.next();
        if(insnNode.getOpcode()===Opcodes.RETURN)
        {
            method.instructions.insertBefore(insnNode,insnList);
            break;
        }
    }
    return method;
}
function transformRenderRightArm(method)
{
    var insnList=new InsnList();
    insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,1));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,2));
    insnList.add(new VarInsnNode(Opcodes.ILOAD,3));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,4));
    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"top/leonx/vanity/PlayerRendererTransform","renderRightArm","(Lnet/minecraft/client/renderer/entity/PlayerRenderer;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V",false));
    insnList.add(new InsnNode(Opcodes.RETURN));
    method.instructions.clear();
    method.instructions.add(insnList);
    return method;
}
function transformRenderLeftArm(method)
{
    var insnList=new InsnList();
    insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,1));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,2));
    insnList.add(new VarInsnNode(Opcodes.ILOAD,3));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,4));
    insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC,"top/leonx/vanity/PlayerRendererTransform","renderLeftArm","(Lnet/minecraft/client/renderer/entity/PlayerRenderer;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V",false));
    insnList.add(new InsnNode(Opcodes.RETURN));
    method.instructions.clear();
    method.instructions.add(insnList);
    return method;
}
function transformCreateNewTileEntity(method)
{
    var insnList=new InsnList();
    var label0 = new Label();
    insnList.add(new LabelNode(label0));
    insnList.add(new TypeInsnNode(Opcodes.NEW,"top/leonx/vanity/tileentity/VanityBedTileEntity"));
    insnList.add(new InsnNode(Opcodes.DUP));
    insnList.add(new VarInsnNode(Opcodes.ALOAD,0));
    insnList.add(new FieldInsnNode(Opcodes.GETFIELD,"net/minecraft/block/BedBlock","color", "Lnet/minecraft/item/DyeColor;"));
    insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL,"top/leonx/vanity/tileentity/VanityBedTileEntity", "<init>", "(Lnet/minecraft/item/DyeColor;)V", false));
    insnList.add(new InsnNode(Opcodes.ARETURN));
    var label1 = new Label();
    insnList.add(new LabelNode(label1));
    method.instructions.clear();
    method.instructions.add(insnList);
    return method;
}
function initializeCoreMod() {
    return {
        'setModelVisibilities': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.PlayerRenderer',
                'methodName': 'func_177137_d',//setModelVisibilities
                'methodDesc': '(Lnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V'
            },
            'transformer': transformSetModelVisibilities
        },
        'renderRightArm()': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.PlayerRenderer',
                'methodName': 'func_229144_a_',//renderRightArm
                'methodDesc': '(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V'
            },
            'transformer': transformRenderRightArm
        },
        'renderLeftArm()': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.entity.PlayerRenderer',
                'methodName': 'func_229146_b_',//renderLeftArm
                'methodDesc': '(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;ILnet/minecraft/client/entity/player/AbstractClientPlayerEntity;)V'
            },
            'transformer': transformRenderLeftArm
        },
        'createNewTileEntity()': {
        'target': {
            'type': 'METHOD',
                'class': 'net.minecraft.block.BedBlock',
                'methodName': 'func_196283_a_',//createNewTileEntity
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;)Lnet/minecraft/tileentity/TileEntity;'
        },
        'transformer': transformCreateNewTileEntity
    }
    }
}