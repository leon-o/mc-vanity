package top.leonx.vanity.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.controller.LookController;
import net.minecraft.entity.ai.controller.MovementController;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;

public class OutsiderMovementController extends MovementController {

    public OutsiderMovementController(MobEntity mob) {
        super(mob);
    }

    @Override
    public void tick() {
        if(!(this.mob instanceof OutsiderEntity)) return;
        OutsiderEntity entity=(OutsiderEntity)this.mob;
        if (this.action == MovementController.Action.STRAFE) {
            float maxMoveSpeed  = entity.getFinalMaxMoveSpeed();
            float navigatorSpeed = (float) this.speed * maxMoveSpeed;
            float moveForwardF = this.moveForward;
            float moveStrafeF = this.moveStrafe;
            float preMoveSpeed = MathHelper.sqrt(moveForwardF * moveForwardF + moveStrafeF * moveStrafeF);
            if (preMoveSpeed < 1.0F) {
                preMoveSpeed = 1.0F;
            }

            preMoveSpeed = navigatorSpeed / preMoveSpeed;
            moveForwardF = moveForwardF * preMoveSpeed;
            moveStrafeF = moveStrafeF * preMoveSpeed;
            float         f5            = MathHelper.sin(this.mob.rotationYaw * ((float) Math.PI / 180F));
            float         f6            = MathHelper.cos(this.mob.rotationYaw * ((float) Math.PI / 180F));
            float         f7            = moveForwardF * f6 - moveStrafeF * f5;
            float         f8            = moveStrafeF * f6 + moveForwardF * f5;
            PathNavigator pathnavigator = this.mob.getNavigator();
            NodeProcessor nodeprocessor = pathnavigator.getNodeProcessor();
            if (nodeprocessor.getPathNodeType(this.mob.world, MathHelper.floor(this.mob.getPosX() + (double) f7), MathHelper.floor(this.mob.getPosY()),
                                              MathHelper.floor(this.mob.getPosZ() + (double) f8)) != PathNodeType.WALKABLE) {
                this.moveForward = 1.0F;
                this.moveStrafe = 0.0F;
                navigatorSpeed = maxMoveSpeed;
            }

            this.mob.setAIMoveSpeed(navigatorSpeed);
            this.mob.setMoveForward(this.moveForward);
            this.mob.setMoveStrafing(this.moveStrafe);
            this.action = MovementController.Action.WAIT;
        } else if (this.action == MovementController.Action.MOVE_TO) {
            this.action = MovementController.Action.WAIT;
            //setMoveTo(-380d,4d,349d,0);
            double deltaX = this.posX - this.mob.getPosX();
            double deltaZ = this.posZ - this.mob.getPosZ();
            double deltaY = this.posY - this.mob.getPosY();
            double distance = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
            if (distance < (double) 2.5000003E-7F) {
                this.mob.setMoveForward(0.0F);
                this.mob.setMoveStrafing(0.0F);
                return;
            }
            this.mob.setAIMoveSpeed((float) (entity.getFinalMaxMoveSpeed()*speed));

            float deltaAngle = (float) (MathHelper.atan2(deltaZ, deltaX) * (double) (180F / (float) Math.PI)) - 90.0F;
            boolean isLooking = this.mob.getLookController().getIsLooking();
            if(isLooking)
            {
                LookController lookCtrl   = mob.getLookController();
                double         lookDeltaX =lookCtrl.getLookPosX()-mob.getPosX();
                double         lookDeltaZ=lookCtrl.getLookPosZ()-mob.getPosZ();

                // 当横向移动时，像真实玩家一样倾斜45度
                double lookAngle=(float) (MathHelper.atan2(lookDeltaZ, lookDeltaX) * (double) (180F / (float) Math.PI)) - 90.0F;
                double lookAngleRadian=Math.toRadians(lookAngle);
                lookAngle-=MathHelper.clamp(0.5*deltaX*Math.cos(lookAngleRadian)+deltaZ*Math.sin(lookAngleRadian),-1,1)*45;
                this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, (float) lookAngle, 10.0F);

                double radianYaw=Math.toRadians(MathHelper.wrapDegrees(mob.rotationYaw));
                Vec3d motion =new Vec3d(deltaX*Math.cos(radianYaw)+deltaZ*Math.sin(radianYaw), deltaY, deltaZ*Math.cos(radianYaw)-deltaX*Math.sin(radianYaw));

                motion=motion.normalize().scale(mob.getAIMoveSpeed());

                this.mob.setMoveStrafing((float) motion.x);
                this.mob.setMoveForward((float) motion.z);
                this.mob.setMoveVertical((float)motion.y);
            }else{
                this.mob.rotationYaw = this.limitAngle(this.mob.rotationYaw, deltaAngle, 12.0F);
                this.mob.setMoveForward(mob.getAIMoveSpeed());
            }

            BlockPos   blockpos   = new BlockPos(this.mob);
            BlockState blockstate = this.mob.world.getBlockState(blockpos);
            Block      block      = blockstate.getBlock();
            VoxelShape voxelshape = blockstate.getCollisionShape(this.mob.world, blockpos);
            if (deltaY > (double) this.mob.stepHeight && deltaX * deltaX + deltaZ * deltaZ < (double) Math.max(1.0F, this.mob.getWidth()) || !voxelshape.isEmpty() && this.mob.getPosY() < voxelshape.getEnd(
                    Direction.Axis.Y) + (double) blockpos.getY() && !block.isIn(BlockTags.DOORS) && !block.isIn(BlockTags.FENCES)) {
                this.mob.getJumpController().setJumping();
                this.action = MovementController.Action.JUMPING;
            }
//                if (mob.onGround) {
//                    this.mob.getJumpController().setJumping();
//                    this.action = MovementController.Action.JUMPING;
//                    this.mob.swingArm(Hand.MAIN_HAND);
//                    //((OutsiderEntity)this.mob).placeBlock(new BlockPos(this.mob).west());
//                }
        } else if (this.action == MovementController.Action.JUMPING) {
            this.mob.setAIMoveSpeed(entity.getFinalMaxMoveSpeed());
            if (this.mob.onGround) {
                this.action = MovementController.Action.WAIT;
            }
        } else {
            this.mob.setMoveForward(0.0F);
        }
    }
}
