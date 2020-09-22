package top.leonx.vanity.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.leonx.vanity.VanityMod;
import top.leonx.vanity.capability.BodyPartCapabilityProvider;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.capability.CharacterStateCapabilityProvider;
import top.leonx.vanity.entity.OutsiderEntity;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.network.CharacterDataSynchronizer;
import top.leonx.vanity.network.VanityEquipDataSynchronizer;
import top.leonx.vanity.util.BodyPartUtil;
import top.leonx.vanity.bodypart.BodyPartGroup;
import top.leonx.vanity.util.CharacterStateUtil;

@Mod.EventBusSubscriber(modid = VanityMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeEventSubscriber {
    @SubscribeEvent
    public static void attachCapabilitiesToEntity(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity || event.getObject() instanceof VillagerEntity || event.getObject() instanceof ZombieEntity || event.getObject() instanceof  OutsiderEntity)
        {
            event.addCapability(new ResourceLocation(VanityMod.MOD_ID, "body_part_cap"), new BodyPartCapabilityProvider());
            event.addCapability(new ResourceLocation(VanityMod.MOD_ID, "char_state_cap"), new CharacterStateCapabilityProvider());
        }

    }

    @SubscribeEvent
    public static void onEntitySpawn(final EntityJoinWorldEvent event) {
        if (event.getWorld().isRemote) return;
        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) event.getEntity();
            if(CharacterStateUtil.hasCharacterState(livingEntity))
            {
                CharacterState characterState=livingEntity.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
                if(characterState.isNeedInit())
                    CharacterStateUtil.initCharacterState(characterState);

                if (livingEntity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity mp = (ServerPlayerEntity) livingEntity;
                    CharacterDataSynchronizer.UpdateDataToClient(mp, characterState, livingEntity.getEntityId());
                }
            }
            if (BodyPartUtil.hasBodyPart(livingEntity)) {
                BodyPartCapability.BodyPartData bodyPartData = livingEntity.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
                if (bodyPartData.isNeedInit()) {

                    if(livingEntity instanceof OutsiderEntity || livingEntity instanceof PlayerEntity)
                    {
                        bodyPartData.getItemStacksList().addAll(BodyPartUtil.getRandomBodyPart(bodyPartData,BodyPartGroup.GROUPS.values().toArray(new BodyPartGroup[]{})));
                    }else {
                        bodyPartData.getItemStacksList().addAll(BodyPartUtil.getRandomBodyPart(bodyPartData,BodyPartGroup.BASE_HAIR_GROUP,BodyPartGroup.EXTRA_HAIR_GROUP));
                    }

                    bodyPartData.setNeedInit(false);
                }
                if (livingEntity instanceof ServerPlayerEntity) {
                    ServerPlayerEntity mp = (ServerPlayerEntity) livingEntity;
                    VanityEquipDataSynchronizer.UpdateDataToClient(mp, bodyPartData, livingEntity.getEntityId());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerStartTracking(PlayerEvent.StartTracking evt) {
        Entity       target = evt.getTarget();
        PlayerEntity player = evt.getPlayer();
        if (player instanceof ServerPlayerEntity && target instanceof LivingEntity) {
            LivingEntity livingBase = (LivingEntity) target;
            if (BodyPartUtil.hasBodyPart(livingBase)) {
                BodyPartCapability.BodyPartData bodyPartData = livingBase.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
                VanityEquipDataSynchronizer.UpdateDataToClient((ServerPlayerEntity) player, bodyPartData, livingBase.getEntityId());
            }
            if(CharacterStateUtil.hasCharacterState(livingBase))
            {
                CharacterState characterState=livingBase.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(CharacterState.EMPTY);
                CharacterDataSynchronizer.UpdateDataToClient((ServerPlayerEntity) player, characterState, livingBase.getEntityId());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone evt) {
        PlayerEntity player    = evt.getPlayer();
        PlayerEntity oldPlayer = evt.getOriginal();
        oldPlayer.revive();
        BodyPartCapability.BodyPartData bodyPartData    = player.getCapability(ModCapabilityTypes.BODY_PART).orElse(new BodyPartCapability.BodyPartData());
        BodyPartCapability.BodyPartData oldBodyPartData = oldPlayer.getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY);
        bodyPartData.getItemStacksList().clear();
        bodyPartData.getItemStacksList().addAll(oldBodyPartData.getItemStacksList());
        bodyPartData.setNeedInit(oldBodyPartData.isNeedInit());

        CharacterState characterState = player.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
        CharacterState oldCharacterState = oldPlayer.getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
        characterState.setRoot(oldCharacterState.getRoot());

        oldPlayer.remove();
    }

    @SubscribeEvent
    public static void onPlayerDamate(final LivingDamageEvent event)
    {
        LivingEntity                 living   = event.getEntityLiving();
        LazyOptional<CharacterState> optional = living.getCapability(ModCapabilityTypes.CHARACTER_STATE);
        if(optional.isPresent())
        {
            CharacterState state = optional.orElse(CharacterState.EMPTY);
            float        percent = living.getHealth() / living.getMaxHealth();
            if(percent>0.9)
                state.setMOOD(CharacterState.MOOD.HAPPY);
            else if(percent>0.7)
                state.setMOOD(CharacterState.MOOD.NORMAL);
            else if(percent>0.5)
                state.setMOOD(CharacterState.MOOD.ANGRY);
            else if(percent>0.3)
                state.setMOOD(CharacterState.MOOD.SAD);
            else if(percent>0.1)
                state.setMOOD(CharacterState.MOOD.SURPRISED);

            CharacterDataSynchronizer.UpdateDataToTracking(living,state);
            if(living instanceof PlayerEntity)
                CharacterDataSynchronizer.UpdateDataToClient((ServerPlayerEntity) living,state,living.getEntityId());
        }
    }
}
