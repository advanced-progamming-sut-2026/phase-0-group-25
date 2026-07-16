package src.Model.PlantsAndZombies.Abilities;

import src.Enums.Status;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;

public class FatalDamage implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;
        Entity rival = zombie.getRival();

        if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
            rival.setCurrentHP(0);

            zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity() / 2);
        } else { //zombies like GARGANTUAR & PIANO & EXPLORER with enlightened torch
            BattlePlant plant = (BattlePlant) rival;
            plant.setCurrentHP(0);

            zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity());
        }
        zombie.setStatus(Status.MOVING);
    }
}
