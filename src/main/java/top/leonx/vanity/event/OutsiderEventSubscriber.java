package top.leonx.vanity.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.network.CharacterDataSynchronizer;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = VanityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OutsiderEventSubscriber {

    @SubscribeEvent
    public static void onOutsiderPickItem(OutsiderEvent.PickItemEvent event)
    {
        UUID throwerId = event.getThrowerId();
        if (throwerId != null) {
            OutsiderEntity outsiderEntity = event.getEntity();
            outsiderEntity.getCharacterState().promoteRelationWith(throwerId, 1);
            CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
        }
    }

    @SubscribeEvent
    public static void onMonsterNearAttackedByPlayer(AttackEntityEvent event)
    {
        Entity target = event.getTarget();
        if(target instanceof LivingEntity)
        {
            LivingEntity livingEntity = (LivingEntity) target;
            if(livingEntity.getAttackingEntity() instanceof OutsiderEntity)
            {
                OutsiderEntity outsiderEntity = (OutsiderEntity) livingEntity;
                outsiderEntity.getCharacterState().promoteRelationWith(event.getPlayer().getUniqueID(),1f);
                CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
            }

            for (OutsiderEntity outsiderEntity : target.getEntityWorld().getEntitiesWithinAABB(OutsiderEntity.class, target.getBoundingBox().expand(10, 10, 10))) {
                outsiderEntity.getCharacterState().promoteRelationWith(event.getPlayer().getUniqueID(),0.5F);
                CharacterDataSynchronizer.UpdateDataToTracking(outsiderEntity, outsiderEntity.getCharacterState());
            }
        }
    }
}
