package src.Model.PlantsAndZombies.Abilities;

import src.Enums.Status;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;

public class Eating implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        double damageAmount = zombie.getZombieStats().getEatdps() * 0.1;
        BattlePlant plant = (BattlePlant) zombie.getRival();

        double plantFinalHP = plant.getCurrentHP() - damageAmount;
        plant.setCurrentHP(plantFinalHP);

        if (!plant.isAlive()) {
            zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity());
            zombie.setStatus(Status.MOVING);
        }

    }
}
