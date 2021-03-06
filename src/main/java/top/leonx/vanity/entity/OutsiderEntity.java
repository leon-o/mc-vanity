package top.leonx.vanity.entity;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.GroundPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.potion.EffectUtils;
import net.minecraft.potion.Effects;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import top.leonx.vanity.ai.OutsiderTasks;
import top.leonx.vanity.bodypart.BodyPartStack;
import top.leonx.vanity.capability.BodyPartCapability;
import top.leonx.vanity.capability.CharacterState;
import top.leonx.vanity.container.OutsiderDialogContainer;
import top.leonx.vanity.event.OutsiderEvent;
import top.leonx.vanity.init.ModCapabilityTypes;
import top.leonx.vanity.init.ModEntityTypes;
import top.leonx.vanity.init.ModSensorTypes;
import top.leonx.vanity.util.FakePlayerHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings({"UnusedReturnValue"})
public class OutsiderEntity extends AbstractOutsider {
    private static final ImmutableList<MemoryModuleType<?>>                                  MEMORY_TYPES = ImmutableList.of(MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.HOME,
                                                                                                                             MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT,
                                                                                                                             MemoryModuleType.MOBS, MemoryModuleType.VISIBLE_MOBS,
                                                                                                                             MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS,
                                                                                                                             MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET,
                                                                                                                             MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET,
                                                                                                                             MemoryModuleType.BREED_TARGET, MemoryModuleType.PATH,
                                                                                                                             MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.field_225462_q,
                                                                                                                             MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY,
                                                                                                                             MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE,
                                                                                                                             MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE,
                                                                                                                             MemoryModuleType.HEARD_BELL_TIME,
                                                                                                                             MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT,
                                                                                                                             MemoryModuleType.field_226332_A_, MemoryModuleType.LAST_WORKED_AT_POI,
                                                                                                                             MemoryModuleType.GOLEM_LAST_SEEN_TIME);
    private static final ImmutableList<SensorType<? extends Sensor<? super OutsiderEntity>>> SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS,
                                                                                                                             SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY,
                                                                                                                             SensorType.GOLEM_LAST_SEEN, ModSensorTypes.OUTSIDER_BED_SENSOR.get(),
                                                                                                                             ModSensorTypes.OUTSIDER_NEAREST_HOSTEL_SENSOR.get());

    public final    OutsiderInteractionManager         interactionManager     = new OutsiderInteractionManager(this);
    protected final PlayerSimPathNavigator             waterNavigator;
    protected final GroundPathNavigator                groundNavigator;
    private         HashSet<DataParameter<?>>          doNotNotifyIncorporeal;
    public          OutsiderInventory                  inventory;
    public          ServerPlayerEntity                 interactingPlayer      = null;
    protected       GeneralFoodStats<AbstractOutsider> foodStats;
    protected       NeedsStatus                        needsStatus;
    protected       PlayerAbilities                    abilities;
    protected       CooldownTracker                    cooldownTracker;
    private         CharacterState                     characterState;
    private         OutsiderIncorporeal                incorporeal;

    public OutsiderEntity(EntityType<? extends OutsiderEntity> type, World world) {
        super(type, world);
        moveController = new OutsiderMovementController(this);
        this.waterNavigator = new PlayerSimPathNavigator(this, world);
        this.groundNavigator = new PlayerSimPathNavigator(this, world);
        this.navigator = groundNavigator;

        foodStats = new GeneralFoodStats<>(this);
        needsStatus = new NeedsStatus(this);
        inventory = new OutsiderInventory(this);
        abilities = new PlayerAbilities();
        cooldownTracker = new CooldownTracker();
    }

    @Override
    public void setUniqueId(UUID uniqueIdIn) {
        super.setUniqueId(uniqueIdIn);
        bindIncorporeal(); //update incorporeal
        if(this.world.isRemote())
            getIncorporeal().setEntityComponent(writeWithoutTypeId(new CompoundNBT()));
    }

    /**
     * Add exhaustion of food status.
     */
    public void addMovementStat(double x, double y, double z) {
        if (!this.isPassenger()) {
            double twiceSum = x * x + y * y + z * z;
            if (this.isSwimming()) {
                int i = Math.round(MathHelper.sqrt(twiceSum) * 100.0F);
                if (i > 0) {
                    //this.addStat(Stats.SWIM_ONE_CM, i);
                    this.addExhaustion(0.01F * (float) i * 0.01F);
                }
            } else if (this.areEyesInFluid(FluidTags.WATER, true)) {
                int j = Math.round(MathHelper.sqrt(twiceSum) * 100.0F);
                if (j > 0) {
                    //this.addStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                    this.addExhaustion(0.01F * (float) j * 0.01F);
                }
            } else if (this.isInWater()) {
                int k = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
                if (k > 0) {
                    //this.addStat(Stats.WALK_ON_WATER_ONE_CM, k);
                    this.addExhaustion(0.01F * (float) k * 0.01F);
                }
//            } else if (this.isOnLadder()) {
////                if (y > 0.0D) {
////                    //this.addStat(Stats.CLIMB_ONE_CM, (int)Math.round(y * 100.0D));
////                }
            } else if (this.onGround) {
                int l = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
                if (l > 0) {
                    if (this.isSprinting()) {
                        //this.addStat(Stats.SPRINT_ONE_CM, l);
                        this.addExhaustion(0.1F * (float) l * 0.01F);
                    } else if (this.isCrouching()) {
                        //this.addStat(Stats.CROUCH_ONE_CM, l);
                        this.addExhaustion(0.0F * (float) l * 0.01F);
                    } else {
                        //this.addStat(Stats.WALK_ONE_CM, l);
                        this.addExhaustion(0.0F * (float) l * 0.01F);
                    }
                }
            } /*else if (this.isElytraFlying()) {
                int i1 = Math.round(MathHelper.sqrt(twiceSum) * 100.0F);
                //this.addStat(Stats.AVIATE_ONE_CM, i1);
            } else {
                int j1 = Math.round(MathHelper.sqrt(x * x + z * z) * 100.0F);
                if (j1 > 25) {
                    //this.addStat(Stats.FLY_ONE_CM, j1);
                }
            }*/

        }
    }

    /**
     * Attack an entity as mob (Copy from player entity)
     *
     * @param targetEntity target
     * @return whether the attack was successful
     */
    public boolean attackEntityAsMob(Entity targetEntity) {
        swingArm(Hand.MAIN_HAND);
        boolean result = false;
        if (targetEntity.canBeAttackedWithItem()) {
            if (!targetEntity.hitByEntity(this)) {
                float entityDamage = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getValue();
                float targetAttribute;
                if (targetEntity instanceof LivingEntity) {
                    targetAttribute = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((LivingEntity) targetEntity).getCreatureAttribute());
                } else {
                    targetAttribute = EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), CreatureAttribute.UNDEFINED);
                }

                float coolingPercentage = this.getAttackCoolingPercentage(0.5F);
                entityDamage = entityDamage * (0.2F + coolingPercentage * coolingPercentage * 0.8F);
                targetAttribute = targetAttribute * coolingPercentage;
                this.resetAttackCooldown();
                if (entityDamage > 0.0F || targetAttribute > 0.0F) {
                    boolean flag      = coolingPercentage > 0.9F;
                    boolean flag1     = false;
                    int     knockBack = 0;
                    knockBack = knockBack + EnchantmentHelper.getKnockbackModifier(this);
                    if (this.isSprinting() && flag) {
                        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                        ++knockBack;
                        flag1 = true;
                    }

                    boolean isCriticalHit = flag && this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(
                            Effects.BLINDNESS) && !this.isPassenger() && targetEntity instanceof LivingEntity;
                    isCriticalHit = isCriticalHit && !this.isSprinting();
                    float criticalHitResult = 1; //TODO Critical Hit
                    if (isCriticalHit) {
                        entityDamage *= criticalHitResult;
                    }

                    entityDamage = entityDamage + targetAttribute;
                    boolean isSweep             = false;
                    double  deltaDistanceWalked = this.distanceWalkedModified - this.prevDistanceWalkedModified;
                    if (flag && !isCriticalHit && !flag1 && this.onGround && deltaDistanceWalked < (double) this.getAIMoveSpeed()) {
                        ItemStack itemstack = this.getHeldItem(Hand.MAIN_HAND);
                        if (itemstack.getItem() instanceof SwordItem) {
                            isSweep = true;
                        }
                    }

                    float   targetHealth = 0.0F;
                    boolean isFire       = false;
                    int     fireModifier = EnchantmentHelper.getFireAspectModifier(this);
                    if (targetEntity instanceof LivingEntity) {
                        targetHealth = ((LivingEntity) targetEntity).getHealth();
                        if (fireModifier > 0 && !targetEntity.isBurning()) {
                            isFire = true;
                            targetEntity.setFire(1);
                        }
                    }

                    Vec3d   targetMotion    = targetEntity.getMotion();
                    boolean isAttackSuccess = targetEntity.attackEntityFrom(DamageSource.causeMobDamage(this), entityDamage);
                    result = isAttackSuccess;
                    if (isAttackSuccess) {
                        if (knockBack > 0) {
                            if (targetEntity instanceof LivingEntity) {
                                ((LivingEntity) targetEntity).knockBack(this, (float) knockBack * 0.5F, MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)),
                                                                        -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)));
                            } else {
                                targetEntity.addVelocity(-MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)) * (float) knockBack * 0.5F, 0.1D,
                                                         MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)) * (float) knockBack * 0.5F);
                            }

                            this.setMotion(this.getMotion().mul(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (isSweep) {
                            float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * entityDamage;

                            for (LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, targetEntity.getBoundingBox().grow(1.0D, 0.25D, 1.0D))) {
                                if (livingentity != this && livingentity != targetEntity && !this.isOnSameTeam(
                                        livingentity) && (!(livingentity instanceof ArmorStandEntity) || !((ArmorStandEntity) livingentity).hasMarker()) && this.getDistanceSq(livingentity) < 9.0D) {
                                    livingentity.knockBack(this, 0.4F, MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F)), -MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F)));
                                    livingentity.attackEntityFrom(DamageSource.causeMobDamage(this), f3);
                                }
                            }

                            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                            this.spawnSweepParticles();
                        }

                        if (targetEntity instanceof ServerPlayerEntity && targetEntity.velocityChanged) {
                            ((ServerPlayerEntity) targetEntity).connection.sendPacket(new SEntityVelocityPacket(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.setMotion(targetMotion);
                        }

                        if (isCriticalHit) {
                            this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                            this.onCriticalHit(targetEntity);
                        }

                        if (!isCriticalHit && !isSweep) {
                            if (flag) {
                                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (targetAttribute > 0.0F) {
                            this.onEnchantmentCritical(targetEntity);
                        }

                        this.setLastAttackedEntity(targetEntity);
                        if (targetEntity instanceof LivingEntity) {
                            EnchantmentHelper.applyThornEnchantments((LivingEntity) targetEntity, this);
                        }

                        EnchantmentHelper.applyArthropodEnchantments(this, targetEntity);
                        ItemStack heldItemMainhand = this.getHeldItemMainhand();
                        Entity    entity           = targetEntity;
                        if (targetEntity instanceof EnderDragonPartEntity) {
                            entity = ((EnderDragonPartEntity) targetEntity).dragon;
                        }

                        if (!this.world.isRemote && !heldItemMainhand.isEmpty() && entity instanceof LivingEntity) {
                            ItemStack copy = heldItemMainhand.copy();

                            if (targetEntity instanceof LivingEntity) {
                                heldItemMainhand.getItem().hitEntity(copy, (LivingEntity) targetEntity, this);
                                if (heldItemMainhand.isEmpty()) {
                                    this.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
                                }
                            }

                        }

                        if (targetEntity instanceof LivingEntity) {
                            float f5 = targetHealth - ((LivingEntity) targetEntity).getHealth();
                            if (fireModifier > 0) {
                                targetEntity.setFire(fireModifier * 4);
                            }

                            if (this.world instanceof ServerWorld && f5 > 2.0F) {
                                int k = (int) ((double) f5 * 0.5D);
                                ((ServerWorld) this.world).spawnParticle(ParticleTypes.DAMAGE_INDICATOR, targetEntity.getPosX(), targetEntity.getPosYHeight(0.5D), targetEntity.getPosZ(), k, 0.1D,
                                                                         0.0D, 0.1D, 0.2D);
                            }
                        }

                        this.addExhaustion(0.1F);
                    } else {
                        this.world.playSound(null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                        if (isFire) {
                            targetEntity.extinguish();
                        }
                    }
                }

            }
        }

        return result;
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        //todo 再说
    }

    @Override
    public boolean canAttack(LivingEntity target) {
        return super.canAttack(target) && getDistance(target) <= getBlockReachDistance();
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        return new OutsiderEntity(ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.get(), this.world);
    }

    /**
     * Called when the shield is broken
     *
     * @param cri is critical hit
     */
    public void disableShield(boolean cri) {
        float f = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
        if (cri) {
            f += 0.75F;
        }

        if (this.rand.nextFloat() < f) {
            this.cooldownTracker.setCooldown(this.getActiveItemStack().getItem(), 100);
            this.resetActiveHand();
            this.world.setEntityState(this, (byte) 30);
        }

    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return inventory.armorInventory;

    }

    public float getAttackCooldownPeriod() {
        return (float) (1.0D / this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getValue() * 20.0D);
    }

    public float getAttackCoolingPercentage(float partialTicks) {
        return MathHelper.clamp(((float) this.ticksSinceLastSwing + partialTicks) / this.getAttackCooldownPeriod(), 0.0F, 1.0F);
    }

    /**
     * get the farthest distance the entity can touch
     */
    public float getBlockReachDistance() {
        float attrib = (float) getAttribute(PlayerEntity.REACH_DISTANCE).getValue();
        return attrib - 0.5F;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Brain<OutsiderEntity> getBrain() {
        return (Brain<OutsiderEntity>) super.getBrain();
    }

    /**
     * Get the character state of this entity.
     *
     * @return character state
     */
    public CharacterState getCharacterState() {
        if (characterState == null) {
            characterState = getCapability(ModCapabilityTypes.CHARACTER_STATE).orElse(new CharacterState());
        }
        return characterState;
    }

    public float getDigSpeed(BlockState state, @Nullable BlockPos pos) {
        float speed = getHeldItemMainhand().getDestroySpeed(state);
        if (speed > 1.0F) {
            int       i         = EnchantmentHelper.getEfficiencyModifier(this);
            ItemStack itemstack = this.getHeldItemMainhand();
            if (i > 0 && !itemstack.isEmpty()) {
                speed += (float) (i * i + 1);
            }
        }

        if (EffectUtils.hasMiningSpeedup(this)) {
            speed *= 1.0F + (float) (EffectUtils.getMiningSpeedup(this) + 1) * 0.2F;
        }

        if (this.isPotionActive(Effects.MINING_FATIGUE)) {
            float multiplyFac;
            //noinspection ConstantConditions
            switch (this.getActivePotionEffect(Effects.MINING_FATIGUE).getAmplifier()) {
                case 0:
                    multiplyFac = 0.3F;
                    break;
                case 1:
                    multiplyFac = 0.09F;
                    break;
                case 2:
                    multiplyFac = 0.0027F;
                    break;
                case 3:
                default:
                    multiplyFac = 8.1E-4F;
            }
            speed *= multiplyFac;
        }

        if (this.areEyesInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) speed /= 5.0F;

        if (!this.onGround) speed /= 5.0F;

        return speed;
    }

    //get fake player for block placing or breaking.
    public ServerPlayerEntity getFakePlayer() {
        if (world.isRemote) return null;
        return FakePlayerHolder.getFakePlayer((ServerWorld) world).get();
    }

    /**
     * @return the entity final speed
     */
    public float getFinalMaxMoveSpeed() {
        double baseSpeed = getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
        if (isSprinting()) baseSpeed *= 1.5;
        else if (isSneaking()) baseSpeed *= 0.3;

        return (float) (3.3 * baseSpeed) * getSpeedFactor();
    }

    @Nullable
    public PlayerEntity getFollowedPlayer() {
        if (world.isRemote) return null;
        Optional<UUID> optionalUUID = getFollowedPlayerId();
        return optionalUUID.map(uuid -> world.getPlayerByUuid(uuid)).orElse(null);
    }

    public void setFollowedPlayer(@Nullable ServerPlayerEntity entity) {
        setFollowedPlayerId(entity != null ? entity.getUniqueID() : null);
    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    /////////////////////////////////////////////
    // Capabilities modify.
    /////////////////////////////////////////////


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        bindIncorporeal();
        LazyOptional<T> incorporealCapability = incorporeal.getCapability(capability, facing);
        if(incorporealCapability.isPresent())
            return incorporealCapability;
        return super.getCapability(capability, facing);
    }

    /////////////////////////////////////////////


    @Override
    public void livingTick() {
        super.livingTick();
        this.updateArmSwingProgress();

        //Pick up item near.
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AxisAlignedBB axisalignedbb;
            if (this.isPassenger() && (this.getRidingEntity() != null && this.getRidingEntity().isAlive())) {
                axisalignedbb = this.getBoundingBox().union(this.getRidingEntity().getBoundingBox()).grow(1.0D, 0.0D, 1.0D);
            } else {
                axisalignedbb = this.getBoundingBox().grow(1.0D, 0.5D, 1.0D);
            }

            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
            for (Entity entity : list) {
                if (entity instanceof ItemEntity && entity.isAlive()) {
                    onCollideWithItemEntity((ItemEntity) entity);
                }
            }
        }
    }

    /**
     * Call when entity collied with item entity.
     * Entity will pick up item.
     */
    public void onCollideWithItemEntity(ItemEntity itemEntity) {
        if (!this.world.isRemote) {


            if (itemEntity.cannotPickup()) return;
            ItemStack itemstack = itemEntity.getItem();
            int       i         = itemstack.getCount();


            ItemStack copy = itemstack.copy();
            if (!itemEntity.cannotPickup() && (itemEntity.getOwnerId() == null || itemEntity.lifespan - itemEntity.age <= 200 || itemEntity.getOwnerId().equals(
                    this.getUniqueID())) && (i <= 0 || inventory.isCanStore(itemstack))) {

                Vec3d posVec     = getPositionVec().add(0, 1, 0);
                Vec3d itemPosVec = itemEntity.getPositionVec();
                Vec3d vec        = posVec.add(itemPosVec.inverse()).normalize().scale(0.15);
                itemEntity.addVelocity(vec.x, vec.y, vec.z);

                if (posVec.distanceTo(itemPosVec) <= 0.7) {
                    boolean cancelled = MinecraftForge.EVENT_BUS.post(new OutsiderEvent.PickItemEvent(this, itemstack, itemEntity.getOwnerId(), itemEntity.getThrowerId()));

                    if (cancelled || !inventory.storeItemStack(itemstack)) return;

                    copy.setCount(copy.getCount() - itemEntity.getItem().getCount());
                    onItemPickup(this, i);

                    if (itemstack.isEmpty()) {
                        onItemPickup(this, i);
                        itemEntity.remove();
                        itemstack.setCount(i);
                    }
                }
            }

        }
    }

    public void onCriticalHit(Entity entityHit) {
    }

    public void onEnchantmentCritical(Entity entityHit) {
    }

    @Override
    public boolean processInteract(PlayerEntity player, Hand hand) {
        boolean successful = super.processInteract(player, hand);
        successful |= player.getHeldItemMainhand().interactWithEntity(player, this, Hand.MAIN_HAND);
        if (!world.isRemote && !successful) {
            INamedContainerProvider provider = new INamedContainerProvider() {
                @Override
                public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
                    return new OutsiderDialogContainer(id, inventory, OutsiderEntity.this);
                }

                @Override
                public ITextComponent getDisplayName() {
                    return OutsiderEntity.this.getDisplayName();
                }
            };
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, t -> t.writeInt(this.getEntityId()));
            interactingPlayer = (ServerPlayerEntity) player;
            successful = true;
        }

        return successful;
    }

    @Override
    public CooldownTracker getCooldownTracker() {
        return cooldownTracker;
    }

    public void resetAttackCooldown() {
        this.ticksSinceLastSwing = 0;
    }

    @SuppressWarnings("unused")
    public void resetBrain(ServerWorld serverWorldIn) {
        Brain<OutsiderEntity> brain = this.getBrain();
        brain.stopAllTasks(serverWorldIn, this);
        this.brain = brain.copy();
        this.initBrain(this.getBrain());
    }

    @Deprecated
    @Override
    public void setAIMoveSpeed(float speedIn) {
        float moveForward = this.moveForward;
        super.setAIMoveSpeed(speedIn);  //moveForward changed in MobEntity
        setMoveForward(moveForward);
    }

    @Override
    public void setItemStackToSlot(EquipmentSlotType slotIn, ItemStack stack) {
        if (slotIn == EquipmentSlotType.MAINHAND) {
            //this.playEquipSound(stack);
            this.inventory.mainInventory.set(this.inventory.mainHandSlotIndex, stack);
        } else if (slotIn == EquipmentSlotType.OFFHAND) {
            //this.playEquipSound(stack);
            this.inventory.offHandInventory.set(0, stack);
        } else if (slotIn.getSlotType() == EquipmentSlotType.Group.ARMOR) {
            this.playEquipSound(stack);
            this.inventory.armorInventory.set(slotIn.getIndex(), stack);
        }
    }

    @Override
    public void setJumping(boolean jumping) {
        super.setJumping(jumping);
        if (this.isSprinting()) {
            this.addExhaustion(0.2F);
        } else {
            this.addExhaustion(0.05F);
        }
    }

    @Deprecated
    @Override
    public void setMoveForward(float amount) {
        super.setMoveForward(amount);
        addMovementStat(0, 0, amount);
    }

    @Deprecated
    @Override
    public void setMoveVertical(float amount) {
        super.setMoveVertical(amount);
        addMovementStat(0, 1, 0);
    }

    @Override
    public void tick() {
        super.tick();
        this.inventory.tick();
        this.interactionManager.tick();
        if (!this.world.isRemote) {
            this.foodStats.tick();
            this.needsStatus.tick();
            tickCooldown();
        }
    }

    public void tickCooldown() {
        ++this.ticksSinceLastSwing;
        this.cooldownTracker.tick();
    }

    public boolean isSecondaryUseActive() {
        return this.isSneaking();
    }

    /**
     * Use {@link OutsiderInteractionManager#useItemInMainHand(Consumer)} instead.
     */
    @Deprecated
    @Override
    public void setActiveHand(Hand hand) {
        super.setActiveHand(hand);
    }

    /**
     * Use {@link OutsiderInteractionManager#stopUseItem()} instead.
     */
    @Deprecated
    @Override
    public void stopActiveHand() {
        super.stopActiveHand();
    }

    @Override
    public GeneralFoodStats<AbstractOutsider> getFoodStats() {
        return foodStats;
    }

    public OutsiderIncorporeal getIncorporeal()
    {
        if(incorporeal==null)
            bindIncorporeal();

        return incorporeal;
    }
    private void bindIncorporeal() {
        if (this.incorporeal != null && this.incorporeal.getRealUniqueId().equals(this.getUniqueID())) return;

        this.incorporeal = OutsiderHolder.getInstance().getOutsider(this.entityUniqueID);
        this.incorporeal.bindTo(this);
        //this.incorporeal.notifier = this::notifyDataManagerChangeFromIncorporeal;
    }
    @Override
    public PlayerAbilities getAbilities() {
        return abilities;
    }

    @Nonnull
    @Override
    public NeedsStatus getNeedsStatus() {
        return needsStatus;
    }

    @Nonnull
    @Override
    public OutsiderInventory getInventory() {
        return inventory;
    }

    @Override
    public CooldownTracker getItemCooldownTracker() {
        return cooldownTracker;
    }

    @Override
    public CompoundNBT writeWithoutTypeId(CompoundNBT compound) {
        CompoundNBT compoundNBT = super.writeWithoutTypeId(compound);
        getIncorporeal().writeComponent(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void read(CompoundNBT compound) {
        if (compound.hasUniqueId("UUID")) {
            this.entityUniqueID = compound.getUniqueId("UUID");
            this.cachedUniqueIdString = this.entityUniqueID.toString();
            super.read(compound);
            getIncorporeal().readComponent(compound);
        } else {
            super.read(compound); //Just summoned.
            bindIncorporeal();
        }
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        writeDataParameter(compound);
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        readDataParameter(compound);
    }

    public void updateSwimming() {
        if (!this.world.isRemote) {
            if (this.isServerWorld() && this.isInWater() && this.targetInWater()) {
                this.navigator = this.waterNavigator;
                this.setSwimming(true);
            } else {
                this.navigator = this.groundNavigator;
                this.setSwimming(false);
            }
        }
    }

    @Override
    public void setRevengeTarget(@Nullable LivingEntity livingBase) {
        if (livingBase instanceof MobEntity && !Objects.equals(((MobEntity) livingBase).getAttackTarget(), this)) return; //I know you didn't mean it.
        super.setRevengeTarget(livingBase);
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        OutsiderHolder.getInstance().joinWorld(this);
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
        if(this.dead && OutsiderHolder.getInstance().outsiderIncorporealMap.containsKey(getUniqueID()))
            respawnEntity();

        OutsiderHolder.getInstance().removedFromWorld(this);
    }

    @Override
    public EntityDataManager getDataManager() {
        return this.dataManager;
    }
    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);

        if(doNotNotifyIncorporeal==null) //Not very elegant
            doNotNotifyIncorporeal=new HashSet<>();

        if (doNotNotifyIncorporeal.contains(key)) {
            doNotNotifyIncorporeal.remove(key);
        } else if (this.incorporeal != null) {
            //noinspection unchecked,rawtypes,rawtypes
            incorporeal.notifyDataManagerChangeFromEntity((DataParameter) key, dataManager.get(key));
        }
    }

    public <T> void notifyDataManagerChangeFromIncorporeal(DataParameter<T> key, T value) {
        if(doNotNotifyIncorporeal==null)
            doNotNotifyIncorporeal=new HashSet<>();

        doNotNotifyIncorporeal.add(key);
        dataManager.set(key, value);
    }

    private void addItemParticles(ItemStack stack, int count) {
        for (int i = 0; i < count; ++i) {
            Vec3d speedvec = new Vec3d(((double) this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
            speedvec = speedvec.rotatePitch(-this.rotationPitch * ((float) Math.PI / 180F));
            speedvec = speedvec.rotateYaw(-this.rotationYawHead * ((float) Math.PI / 180F));
            double d0     = (double) (-this.rand.nextFloat()) * 0.6D - 0.3D;
            Vec3d  posVec = new Vec3d(((double) this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.4D);
            posVec = posVec.rotatePitch(-this.rotationPitch * ((float) Math.PI / 180F));
            posVec = posVec.rotateYaw(-this.rotationYawHead * ((float) Math.PI / 180F));
            posVec = posVec.add(this.getPosX(), this.getPosYEye(), this.getPosZ());
            if (this.world instanceof ServerWorld) //Forge: Fix MC-2518 spawnParticle is nooped on server, need to use server specific variant
                ((ServerWorld) this.world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, stack), posVec.x, posVec.y, posVec.z, 1, speedvec.x, speedvec.y + 0.05D, speedvec.z, 0.0D);
            else this.world.addParticle(new ItemParticleData(ParticleTypes.ITEM, stack), posVec.x, posVec.y, posVec.z, speedvec.x, speedvec.y + 0.05D, speedvec.z);
        }

    }
    //////

    // SHIELD

    //////


    protected void blockUsingShield(LivingEntity entityIn) {
        super.blockUsingShield(entityIn);
        if (entityIn.getHeldItemMainhand().canDisableShield(this.activeItemStack, this, entityIn)) {
            this.disableShield(true);
        }

    }

    @Override
    protected Brain<OutsiderEntity> createBrain(Dynamic<?> dynamicIn) {
        Brain<OutsiderEntity> brain = new Brain<>(MEMORY_TYPES, SENSOR_TYPES, dynamicIn);
        this.initBrain(brain);
        return brain;
    }

    @Override
    protected PathNavigator createNavigator(World worldIn) {
        return groundNavigator;
    }

    protected void damageArmor(float damage) {
        this.inventory.damageArmor(damage);
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) { //Copy from LivingEntity
        if (!this.isInvulnerableTo(damageSrc)) {
            damageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, damageSrc, damageAmount);
            if (damageAmount <= 0) return;
            damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
            damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
            float modifiedDamageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAmount - modifiedDamageAmount));
            float f = damageAmount - modifiedDamageAmount;
            if (f > 0.0F && f < 3.4028235E37F && damageSrc.getTrueSource() instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) damageSrc.getTrueSource()).addStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
            }

            modifiedDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, damageSrc, modifiedDamageAmount);
            if (modifiedDamageAmount != 0.0F) {
                float f1 = this.getHealth();
                if (!(damageSrc.getTrueSource() instanceof MobEntity && ((MobEntity) damageSrc.getTrueSource()).getAttackTarget() != this)) //I know you didn't mean it.
                    this.getCombatTracker().trackDamage(damageSrc, f1, modifiedDamageAmount);
                this.setHealth(f1 - modifiedDamageAmount); // Forge: moved to fix MC-121048
                this.setAbsorptionAmount(this.getAbsorptionAmount() - modifiedDamageAmount);
            }
        }
        this.addExhaustion(damageSrc.getHungerDamage());
    }

    protected void damageShield(float damage) {
        if (damage >= 3.0F && this.activeItemStack.isShield(this)) {
            int  i    = 1 + MathHelper.floor(damage);
            Hand hand = this.getActiveHand();
            this.activeItemStack.damageItem(i, this, (entity) -> entity.sendBreakAnimation(hand));
            if (this.activeItemStack.isEmpty()) {
                if (hand == Hand.MAIN_HAND) {
                    this.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
                } else {
                    this.setItemStackToSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
                }

                this.activeItemStack = ItemStack.EMPTY;
                this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
            }
        }

    }

    @Override
    protected void dropInventory() {
        if (!world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) inventory.dropAllItems();
    }

    private void initBrain(Brain<OutsiderEntity> brain) {
        //float f = getFinalMoveSpeed();
        brain.registerActivity(Activity.CORE, OutsiderTasks.protectPlayer());
        brain.registerActivity(Activity.IDLE, OutsiderTasks.daily());
        //brain.registerActivity(Activity.IDLE, ImmutableList.of(Pair.of(2, new FirstShuffledTask<>(ImmutableList.of(Pair.of(new WalkToTargetTask(200), 1), Pair.of(new FindWalkTargetTask(), 1))))));


        //brain.setDefaultActivities(ImmutableSet.of(Activity.IDLE));
        brain.setFallbackActivity(Activity.IDLE);
        brain.switchTo(Activity.IDLE);
        brain.updateActivity(this.world.getDayTime(), this.world.getGameTime());
    }

    /**
     * Called when item use finished.
     * If the onItemUseFinished isn't null, it will be called.
     * {@link OutsiderInteractionManager#useItemInMainHand}
     */
    @Override
    protected void onItemUseFinish() {
        super.onItemUseFinish();
        interactionManager.itemUseFinished();
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1);
        this.getAttributes().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
        this.getAttributes().registerAttribute(PlayerEntity.REACH_DISTANCE);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.1D);
        this.getAttribute(SWIM_SPEED).setBaseValue(5F);
    }

    @Override
    protected void registerData() {
        super.registerData();
        registerDataParameter();
    }

    /**
     * Spawn a duplicate entity in spawn point.
     */
    private void respawnEntity() {
        if (!world.isRemote()) {
            OutsiderEntity outsiderEntity = ModEntityTypes.OUTSIDER_ENTITY_ENTITY_TYPE.get().create(world.getWorld());
            if (outsiderEntity != null) {
                //copy capabilities.
                outsiderEntity.getCharacterState().setRoot(getCharacterState().getRoot().copy());
                List<BodyPartStack>             originalBodyParts = getCapability(ModCapabilityTypes.BODY_PART).orElse(BodyPartCapability.BodyPartData.EMPTY).getItemStacksList();
                BodyPartCapability.BodyPartData newBodyPartData   = outsiderEntity.getCapability(ModCapabilityTypes.BODY_PART).orElse(new BodyPartCapability.BodyPartData());
                newBodyPartData.getItemStacksList().addAll(originalBodyParts);
                newBodyPartData.setNeedInit(false);

                if (world.getGameRules().get(GameRules.KEEP_INVENTORY).get()) outsiderEntity.inventory.copyInventory(this.inventory);

                outsiderEntity.setCustomName(this.getCustomName());
                outsiderEntity.enablePersistence();
                BlockPos spawnPos = getBedPosition().orElse(world.getSpawnPoint());
                outsiderEntity.setLocationAndAngles((double) spawnPos.getX() + 0.5D, spawnPos.getY(), (double) spawnPos.getZ() + 0.5D, 0.0F, 0.0F);
                outsiderEntity.onInitialSpawn(world, world.getDifficultyForLocation(spawnPos), SpawnReason.STRUCTURE, null, null);
                outsiderEntity.setUniqueId(this.getUniqueID());
                world.addEntity(outsiderEntity);
            }
        }
    }

    /**
     * Spawn the sweep particles that sword created.
     */
    protected void spawnSweepParticles() {
        double d0 = -MathHelper.sin(this.rotationYaw * ((float) Math.PI / 180F));
        double d1 = MathHelper.cos(this.rotationYaw * ((float) Math.PI / 180F));
        if (this.world instanceof ServerWorld) {
            ((ServerWorld) this.world).spawnParticle(ParticleTypes.SWEEP_ATTACK, this.getPosX() + d0, this.getPosYHeight(0.5D), this.getPosZ() + d1, 0, d0, 0.0D, d1, 0.0D);
        }
    }

    private boolean targetInWater() {
        /*if (this.swimmingUp) {
            return true;
        } else {*/

        LivingEntity livingentity = this.getAttackTarget();
        if (livingentity != null && livingentity.isInWater()) {
            return true;
        }
        BlockPos targetPos = getNavigator().getTargetPos();

        //noinspection ConstantConditions
        if (targetPos == null) return false;
        IFluidState fluidState = world.getFluidState(targetPos);
        return !fluidState.getBlockState().isSolid() && fluidState.getFluid() == Fluids.WATER;


        /*}*/
    }

    @Override
    protected void triggerItemUseEffects(ItemStack item, int count) {
        if (!item.isEmpty() && this.isHandActive()) {
            if (item.getUseAction() == UseAction.DRINK) {
                this.playSound(this.getDrinkSound(item), 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (item.getUseAction() == UseAction.EAT) {
                this.addItemParticles(item, count);
                this.playSound(this.getEatSound(item), 0.5F + 0.5F * (float) this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    protected void updateAITasks() {
        this.world.getProfiler().startSection("brain");
        this.getBrain().tick((ServerWorld) this.world, this);
        this.world.getProfiler().endSection();
    }
}
