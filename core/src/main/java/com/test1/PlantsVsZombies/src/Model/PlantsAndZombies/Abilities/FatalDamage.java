package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class FatalDamage implements Ability {
    private boolean isActivated = false;

    @Override
    public void executeAbility(Entity entity) {
        if (this.isActivated) {
            Zombie zombie = (Zombie) entity;
            Entity rival = zombie.getRival();
            rival.setCurrentHP(0);

            if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
                zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity() / 2);
            }

            this.isActivated = false;
            makeMovingActivated(zombie);
        }
    }

    private void makeMovingActivated(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(true);
            }
        }
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }
}
