package tfar.warrior.client;

import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import tfar.warrior.WarriorEntity;

public class WarriorModel extends HumanoidModel<WarriorEntity> {
    public WarriorModel(ModelPart pRoot) {
        super(pRoot);
    }

    @Override
    public void prepareMobModel(WarriorEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTick) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemstack = pEntity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemstack.is(Items.BOW) && pEntity.isAggressive()) {
            if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
            }
        }

        if (itemstack.is(Items.TRIDENT) && pEntity.isAggressive()) {
            if (pEntity.getMainArm() == HumanoidArm.RIGHT) {
                this.rightArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
            } else {
                this.leftArmPose = HumanoidModel.ArmPose.THROW_SPEAR;
            }
        }

        super.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
    }

    @Override
    public void setupAnim(WarriorEntity pEntity, float pLimbSwing, float pLimbSwingAmount, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        WarriorEntity.WarriorPose warriorPose = pEntity.getArmPose();

        this.head.yRot = pNetHeadYaw * (float) Math.PI / 180F;
        this.head.xRot = pHeadPitch * (float) Math.PI / 180F;

      //  if (false)
            if (this.riding) {
                this.rightArm.xRot = -(float) Math.PI / 5F;
                this.rightArm.yRot = 0.0F;
                this.rightArm.zRot = 0.0F;
                this.leftArm.xRot = -(float) Math.PI / 5F;
                this.leftArm.yRot = 0.0F;
                this.leftArm.zRot = 0.0F;
                this.rightLeg.xRot = -1.4137167F;
                this.rightLeg.yRot = (float) Math.PI / 10F;
                this.rightLeg.zRot = 0.07853982F;
                this.leftLeg.xRot = -1.4137167F;
                this.leftLeg.yRot = -(float) Math.PI / 10F;
                this.leftLeg.zRot = -0.07853982F;
            } else {
                this.rightArm.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 2.0F * pLimbSwingAmount * 0.5F;
                this.rightArm.yRot = 0.0F;
                this.rightArm.zRot = 0.0F;
                this.leftArm.xRot = Mth.cos(pLimbSwing * 0.6662F) * 2.0F * pLimbSwingAmount * 0.5F;
                this.leftArm.yRot = 0.0F;
                this.leftArm.zRot = 0.0F;
                this.rightLeg.xRot = Mth.cos(pLimbSwing * 0.6662F) * 1.4F * pLimbSwingAmount * 0.5F;
                this.rightLeg.yRot = 0.0F;
                this.rightLeg.zRot = 0.0F;
                this.leftLeg.xRot = Mth.cos(pLimbSwing * 0.6662F + (float) Math.PI) * 1.4F * pLimbSwingAmount * 0.5F;
                this.leftLeg.yRot = 0.0F;
                this.leftLeg.zRot = 0.0F;
            }
        switch (warriorPose) {
            case MELEE_ATTACK:
                break;
            case BOW_AND_ARROW:
                this.rightArm.yRot = -0.1F + this.head.yRot;
                this.rightArm.xRot = (-(float) Math.PI / 2F) + this.head.xRot;
                this.leftArm.xRot = -0.9424779F + this.head.xRot;
                this.leftArm.yRot = this.head.yRot - 0.4F;
                this.leftArm.zRot = (float) Math.PI / 2F;
                break;
            case CROSSBOW_HOLD:
                AnimationUtils.animateCrossbowHold(this.rightArm, this.leftArm, this.head, true);
                break;
            case CROSSBOW_CHARGE:
                AnimationUtils.animateCrossbowCharge(this.rightArm, this.leftArm, pEntity, true);
                break;
            case TRIDENT:
                if (this.leftArmPose == ArmPose.THROW_SPEAR) {
                    this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float) Math.PI;
                    this.leftArm.yRot = 0.0F;
                }

                if (this.rightArmPose == ArmPose.THROW_SPEAR) {
                    this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float) Math.PI;
                    this.rightArm.yRot = 0.0F;
                }
                break;
        }

            if (warriorPose == WarriorEntity.WarriorPose.BOW_AND_ARROW)
                super.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);

        if (warriorPose == WarriorEntity.WarriorPose.MELEE_ATTACK) {
            AnimationUtils.animateZombieArms(this.leftArm, this.rightArm, pEntity.isAggressive(), this.attackTime, pAgeInTicks);
        } else if (warriorPose == WarriorEntity.WarriorPose.MELEE_ATTACK_WEAPON) {
            holdWeaponHigh(pEntity);
        }
    }

    private void holdWeaponHigh(WarriorEntity pMob) {
        if (pMob.isLeftHanded()) {
            this.leftArm.xRot = -1.8F;
        } else {
            this.rightArm.xRot = -1.8F;
        }
    }

}
