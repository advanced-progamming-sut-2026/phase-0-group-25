package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class Eating implements Ability {
    private static int SNORKEL_X_LIMIT = 1420;
    private boolean isActivated = false;

    @Override
    public void executeAbility(Entity entity) {
        if (this.isActivated) {
            Zombie zombie = (Zombie) entity;
            double damageAmount = zombie.getZombieStats().getEatdps() * 0.1;
            if (zombie.isHypnotized()) {
                if (zombie.getRival() instanceof Zombie) {
                    Zombie target = (Zombie) zombie.getRival();

                    target.takeDamage(damageAmount);
                    checkTargetLife(zombie, target);
                    return;
                } else {
                    this.isActivated = false;
                    makeMovingActivated(zombie);
                    zombie.setRival(null);
                }
                return;

            }

            if (zombie.getRival() instanceof Zombie) {
                Zombie target = (Zombie) zombie.getRival();

                target.takeDamage(damageAmount);
                checkTargetLife(zombie, target);
                return;
            }
            BattlePlant plant = (BattlePlant) zombie.getRival();

            plant.takeDamage(damageAmount);


            if (!plant.isAlive() || plant.getCurrentHP() <= 0) {
                this.isActivated = false;
                makeMovingActivated(zombie);

                if (zombie.getZombieStats().getName().equals("SNORKEL") &&
                    (zombie.getPosition().getX() >= SNORKEL_X_LIMIT)) {
                    zombie.getZombieStats().setSubmarine(true);
                }
            }

            checkWallnutAndExplosive(zombie, plant);
        }
    }

    private void makeMovingActivated(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(true);
            }
        }
    }

    private void checkWallnutAndExplosive(Zombie zombie, BattlePlant plant) {
        if ((plant.getPlantStats().getAbilities().contains("wall-nut")) ||
            (plant.getPlantStats().getAbilities().contains("explosion"))) {
            for (Ability ability : plant.getOriginalAbilities()) {
                if ((ability instanceof WallNutAbility) ||
                    (ability instanceof Explosion)) {
                    ability.executeAbility(zombie);
                }
            }
        }
    }

    private void checkTargetLife(Zombie zombie, Zombie target) {
        if (!target.isAlive()) {
            this.isActivated = false;
            makeMovingActivated(zombie);
        }
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
