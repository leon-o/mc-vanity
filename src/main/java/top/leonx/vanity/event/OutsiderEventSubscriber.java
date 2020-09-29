package top.leonx.vanity.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModParticleTypes;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.util.AIUtil;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = VanityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OutsiderEventSubscriber {

    @SubscribeEvent
    public static void onMonsterNearAttackedByPlayer(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (target instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) target;

            if (livingEntity.getAttackingEntity() instanceof OutsiderEntity) {
                OutsiderEntity outsiderEntity = (OutsiderEntity) livingEntity;
                CharacterState.promoteRelationWith(event.getPlayer(),outsiderEntity, 1f);

                if(!target.world.isRemote())
                {
                    CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
                }
            }

            for (OutsiderEntity outsiderEntity : target.getEntityWorld().getEntitiesWithinAABB(OutsiderEntity.class, target.getBoundingBox().expand(10, 10, 10))) {
                CharacterState.promoteRelationWith(event.getPlayer(),outsiderEntity, 0.5F);

                if(!target.world.isRemote())
                {
                    CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
                }
            }

        }
    }
    @SubscribeEvent
    public static void onPlayerDropItem(ItemTossEvent event)
    {
        event.getEntityItem().setThrowerId(event.getPlayer().getUniqueID());
    }
    @SubscribeEvent
    public static void onOutsiderPickItem(OutsiderEvent.PickItemEvent event) {
        UUID throwerId = event.getThrowerId();
        if (throwerId != null) {
            OutsiderEntity outsiderEntity = event.getEntity();

            ItemStack itemStack    = event.getItemStack();
            float     relaIncrease = (float) (0.02 * AIUtil.getItemValue(itemStack) * itemStack.getCount());
            boolean modified=false;
            PlayerEntity player=outsiderEntity.world.getPlayerByUuid(throwerId);
            if(relaIncrease!=0)
            {
                CharacterState.promoteRelationWith(outsiderEntity,player, relaIncrease);
                modified=true;
            }


            float loveIncrease=0;
            if(player!=null &&player.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY).getGender().equals(outsiderEntity.getCharacterState().getSexualOrientation()))
            {
                loveIncrease=(float) (0.02 * AIUtil.getLoveValue(itemStack) * itemStack.getCount());

                if(loveIncrease!=0)
                {
                    CharacterState.promoteLoveWith(outsiderEntity,player, loveIncrease);
                    modified=true;
                }
            }

            if(!outsiderEntity.world.isRemote())
            {
                //Vec3d pos = outsiderEntity.getEyePosition(1f);
                for (int i = 0; i < relaIncrease; i++) {
                    ((ServerWorld) outsiderEntity.world).spawnParticle(ModParticleTypes.GREEN_HEART.get(), outsiderEntity.getPosXRandom(1), outsiderEntity.getPosYRandom()+1 ,
                                                                       outsiderEntity.getPosZRandom(1), 1, 0,
                                                                       0.1, 0, 0.1);
                }

                for (int i = 0; i < loveIncrease; i++) {
                    ((ServerWorld) outsiderEntity.world).spawnParticle(ModParticleTypes.PINK_HEART.get(), outsiderEntity.getPosXRandom(1), outsiderEntity.getPosYRandom()+1 ,
                                                                       outsiderEntity.getPosZRandom(1), 1, 0,
                                                                       0.1, 0, 0.1);
                }
            }

            if(modified)
                CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
        }
    }
}
