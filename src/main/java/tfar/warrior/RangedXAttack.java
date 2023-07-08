package tfar.warrior;

import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal;
import net.minecraft.world.entity.monster.CrossbowAttackMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;

public class RangedXAttack<T extends Monster & RangedAttackMob & CrossbowAttackMob> extends RangedCrossbowAttackGoal<T> {
    private final T pMob;

    public RangedXAttack(T mob, double pSpeedModifier, float pAttackRadius) {
        super(mob, pSpeedModifier, pAttackRadius);
        this.pMob = mob;
    }

    @Override
    public void start() {
        super.start();
        pMob.setAggressive(true);
    }
}
