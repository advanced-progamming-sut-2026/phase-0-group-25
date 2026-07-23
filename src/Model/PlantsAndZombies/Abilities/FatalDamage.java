package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;

public class FatalDamage implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;
        Entity rival = zombie.getRival();

        rival.setCurrentHP(0);

        if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
            zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity() / 2);

        } else { //zombies like GARGANTUAR & PIANO & EXPLORER with enlightened torch
            zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity());

        }
        zombie.setStatus(Status.MOVING);
    }
}
