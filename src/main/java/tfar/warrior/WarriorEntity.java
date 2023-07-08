package tfar.warrior;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.Optional;

public class WarriorEntity extends Zombie implements CrossbowAttackMob {

    private final RangedBowAttackGoal<WarriorEntity> bowGoal = new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F);

    private final RangedCrossbowAttackGoal<WarriorEntity> crossbowGoal = new RangedXAttack<>(this, 1.0D, 8.0F);

    private final MeleeAttackGoal meleeGoal = new MeleeAttackGoal(this, 1.2D, false);

    private final TridentAttackGoal tridentGoal = new TridentAttackGoal(this, 1.0D, 40, 10.0F);


    private static final EntityDataAccessor<Boolean> DATA_IS_CHARGING_CROSSBOW = SynchedEntityData.defineId(WarriorEntity.class, EntityDataSerializers.BOOLEAN);
    private static final float CROSSBOW_POWER = 1.6F;


    public WarriorEntity(EntityType<? extends WarriorEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Override
    protected void registerGoals() {
      //  this.goalSelector.addGoal(4, new Zombie.ZombieAttackTurtleEggGoal(this, 1.0D, 3));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        //this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, false, 4, () -> true));
        this.goalSelector.addGoal(2,new OpenDoorGoal(this,true));

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        //this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Piglin.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    protected void addBehaviourGoals() {

    }



    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_IS_CHARGING_CROSSBOW, false);
    }

    private boolean isChargingCrossbow() {
        return this.entityData.get(DATA_IS_CHARGING_CROSSBOW);
    }

    @Override
    public void setChargingCrossbow(boolean pIsCharging) {
        this.entityData.set(DATA_IS_CHARGING_CROSSBOW, pIsCharging);
    }

    @Override
    public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
        if (this.getMainHandItem().getItem() instanceof net.minecraft.world.item.BowItem) {
            shootBowProjectile(pTarget, pDistanceFactor);
        } else if (this.getMainHandItem().getItem() instanceof CrossbowItem) {
            this.performCrossbowAttack(this, CROSSBOW_POWER);
        } else {
            throwTrident(pTarget, pDistanceFactor);
        }
    }

    public void shootBowProjectile(LivingEntity pTarget, float pDistanceFactor) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> item instanceof net.minecraft.world.item.BowItem)));
        AbstractArrow abstractarrow = this.getArrow(itemstack, pDistanceFactor);
        abstractarrow = ((BowItem)this.getMainHandItem().getItem()).customArrow(abstractarrow);
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(1/3D) - abstractarrow.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        abstractarrow.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrow);
    }

    public void throwTrident(LivingEntity pTarget, float pDistanceFactor) {
        ThrownTrident throwntrident = new ThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = pTarget.getX() - this.getX();
        double d1 = pTarget.getY(1/3D) - throwntrident.getY();
        double d2 = pTarget.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        throwntrident.shoot(d0, d1 + d3 * (double)0.2F, d2, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(throwntrident);
    }


    protected AbstractArrow getArrow(ItemStack pArrowStack, float pVelocity) {
        return ProjectileUtil.getMobArrow(this, pArrowStack, pVelocity);
    }

    @Override
    public void shootCrossbowProjectile(LivingEntity pTarget, ItemStack pCrossbowStack, Projectile pProjectile, float pProjectileAngle) {
        this.shootCrossbowProjectile(this, pTarget, pProjectile, pProjectileAngle, CROSSBOW_POWER);
    }

    public boolean canFireProjectileWeapon(ProjectileWeaponItem pProjectileWeapon) {
        return pProjectileWeapon == Items.CROSSBOW || pProjectileWeapon == Items.BOW;
    }

    @Override
    public void onCrossbowAttackPerformed() {
        this.noActionTime = 0;
    }

    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        if (pReason != MobSpawnType.STRUCTURE) {
                this.setItemSlot(EquipmentSlot.MAINHAND, this.createSpawnWeapon());
        }
        ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);

        //this.populateDefaultEquipmentSlots(pDifficulty);
        this.populateDefaultEquipmentEnchantments(pLevel.getRandom(),pDifficulty);
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }

    public void reassessWeaponGoal() {
        if (!this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeGoal);
            this.goalSelector.removeGoal(this.bowGoal);
            this.goalSelector.removeGoal(this.crossbowGoal);
            this.goalSelector.removeGoal(this.tridentGoal);
            ItemStack itemstack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, item -> (item instanceof BowItem || item instanceof CrossbowItem || item instanceof TridentItem)));
            if (itemstack.is(Items.BOW)) {
                int i = 20;
                if (this.level.getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowGoal.setMinAttackInterval(i);
                this.goalSelector.addGoal(2, this.bowGoal);
            } else if (itemstack.is(Items.CROSSBOW)) {
                this.goalSelector.addGoal(2, this.crossbowGoal);
            } else if (itemstack.is(Items.TRIDENT)) {
                this.goalSelector.addGoal(2, this.tridentGoal);
            }
            else {
                this.goalSelector.addGoal(2, this.meleeGoal);
            }
        }
    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.reassessWeaponGoal();
    }

    public void setItemSlot(EquipmentSlot pSlot, ItemStack pStack) {
        super.setItemSlot(pSlot, pStack);
        if (!this.level.isClientSide) {
            this.reassessWeaponGoal();
        }

    }

    private ItemStack createSpawnWeapon() {
        Optional<WeightedEntry.Wrapper<Item>> item = Warrior.randomList.getRandom(random);
        ItemStack stack = new ItemStack(item.get().getData());
        return stack;
    }

    @Override
    public void setBaby(boolean pChildZombie) {
        //do nothing
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.HUSK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.HUSK_STEP;
    }

    public WarriorPose getArmPose() {
        if (this.isChargingCrossbow()) {
            return WarriorPose.CROSSBOW_CHARGE;
        } else if (this.isHolding(is -> is.getItem() instanceof CrossbowItem)) {
            boolean aggressive = isAggressive();
            return aggressive ? WarriorPose.CROSSBOW_HOLD : WarriorPose.CROSSBOW_HOLD_IDLE;
        } else if (this.isHolding(is -> is.getItem() instanceof BowItem)) {
            return WarriorPose.BOW_AND_ARROW;
        } else if (this.isHolding(is -> is.getItem() instanceof TridentItem)) {
            return WarriorPose.TRIDENT;
         } else if (isHolding(is -> !is.isEmpty())){
            return isAggressive() ? WarriorPose.MELEE_ATTACK_WEAPON : WarriorPose.NEUTRAL;
        } else {
            return WarriorPose.MELEE_ATTACK;
        }
    }

    static class TridentAttackGoal extends RangedAttackGoal {
        private final WarriorEntity drowned;

        public TridentAttackGoal(RangedAttackMob pRangedAttackMob, double pSpeedModifier, int pAttackInterval, float pAttackRadius) {
            super(pRangedAttackMob, pSpeedModifier, pAttackInterval, pAttackRadius);
            this.drowned = (WarriorEntity) pRangedAttackMob;
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        public boolean canUse() {
            return super.canUse() && this.drowned.getMainHandItem().is(Items.TRIDENT);
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void start() {
            super.start();
           // this.drowned.setAggressive(true);
            this.drowned.startUsingItem(InteractionHand.MAIN_HAND);
        }

        /**
         * Reset the task's internal state. Called when this task is interrupted by another one
         */
        public void stop() {
            super.stop();
            this.drowned.stopUsingItem();
        //    this.drowned.setAggressive(false);
        }
    }

    public enum WarriorPose{
        MELEE_ATTACK,
        MELEE_ATTACK_WEAPON,
        BOW_AND_ARROW,
        CROSSBOW_HOLD_IDLE,

        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        TRIDENT,
        NEUTRAL;
    }

    public static final EntityType<WarriorEntity> WARRIOR = EntityType.Builder.of(WarriorEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F)
            .clientTrackingRange(8).build("warrior");

    public static final Item WARRIOR_SPAWN_EGG = new SpawnEggItem(WARRIOR, 0x8ff1d7, 0x799c65,new Item.Properties().tab(CreativeModeTab.TAB_MISC));

    public static final TagKey<Biome> BIOMES = TagKey.create(Registry.BIOME_REGISTRY,new ResourceLocation(Warrior.MODID,"warrior_spawns"));

}
