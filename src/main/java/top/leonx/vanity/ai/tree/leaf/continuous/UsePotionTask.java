package top.leonx.vanity.ai.tree.leaf.continuous;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.potion.*;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import top.leonx.vanity.ai.tree.BehaviorTreeTask;
import top.leonx.vanity.entity.OutsiderEntity;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public class UsePotionTask extends BehaviorTreeTask<OutsiderEntity> {
    Function<OutsiderEntity, LivingEntity> targetGetter;

    public UsePotionTask(Function<OutsiderEntity, LivingEntity> targetGetter) {
        this.targetGetter = targetGetter;
    }
    LivingEntity target;
    boolean selfUse;
    ItemStack heldPotion;
    int countOnStartUse;

    @Override
    protected void onStart(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        target = targetGetter.apply(entity);
        if(target==null)
        {
            submitResult(Result.FAIL);
            return;
        }
        selfUse=target.equals(entity);
        boolean isHeldPotion = entity.inventory.findAndHeld(Hand.MAIN_HAND, (ItemStack t)->{
            if(!selfUse && !(t.getItem() instanceof ThrowablePotionItem))
                return false;
            List<EffectInstance> effectInstances = PotionUtils.getEffectsFromStack(t);
            return effectInstances.stream().anyMatch(p->p.getPotion().equals(Effects.HEALTH_BOOST)||p.getPotion().equals(Effects.INSTANT_HEALTH));
        }, Comparator.comparingDouble(t->Math.random()));

        if(!isHeldPotion)
            submitResult(Result.FAIL);

        heldPotion=entity.getHeldItemMainhand();
        countOnStartUse=heldPotion.getCount();
    }

    @Override
    protected void onUpdate(ServerWorld world, OutsiderEntity entity, long executionDuration) {
        Vec3d targetPos;
        if(heldPotion.isEmpty() || heldPotion.getCount()<countOnStartUse){
            submitResult(Result.SUCCESS);
            return;
        }

        if(selfUse)
        {
            if(heldPotion.getItem() instanceof ThrowablePotionItem)
            {
                targetPos = entity.getPositionVec();
            }else{
                if(!entity.isHandActive())
                    entity.interactionManager.useItemInMainHand(e->{
                        List<EffectInstance> potions = PotionUtils.getEffectsFromStack(heldPotion);
                        for (EffectInstance potion : potions) {
                            e.addPotionEffect(potion);
                        }

                        heldPotion.shrink(1);
                    });

                return;
            }
        }else{
            targetPos = target.getEyePosition(0.5f);
        }

        entity.getLookController().setLookPosition(targetPos.x,targetPos.y,targetPos.z,20f,100f);
        if(entity.getLookVec().dotProduct(targetPos.add(entity.getEyePosition(0.5f).inverse()).normalize())>0.98)
        {
            Vec3d  vec3d   = target.getMotion();
            double targetX = targetPos.x + vec3d.x - entity.getPosX();
            double targetY = targetPos.y - (double)1.1F - entity.getPosY();
            double targetZ = targetPos.z + vec3d.z - entity.getPosZ();
            float  dist   = MathHelper.sqrt(targetX * targetX + targetZ * targetZ);

            PotionEntity potionentity = new PotionEntity(entity.world, entity);
            potionentity.setItem(PotionUtils.addPotionToItemStack(heldPotion, PotionUtils.getPotionFromItem(heldPotion)));
            potionentity.rotationPitch -= -20.0F;
            potionentity.shoot(targetX, targetY + (double)(dist * 0.2F), targetZ, 0.75F, 8.0F);
            entity.world.playSound(null, entity.getPosX(), entity.getPosY(), entity.getPosZ(), SoundEvents.ENTITY_WITCH_THROW, entity.getSoundCategory(), 1.0F,
                                   0.8F + (float) Math.random() * 0.4F);
            entity.world.addEntity(potionentity);

            entity.getHeldItemMainhand().shrink(1);

            submitResult(Result.SUCCESS);
        }
    }

    @Override
    protected void onEnd(ServerWorld world, OutsiderEntity entity, long executionDuration) {

    }
}
