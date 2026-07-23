package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;

public class Eating implements Ability {
    private static int SNORKEL_X_LIMIT = 1420;

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
            if (zombie.getZombieStats().getName().equals("SNORKEL") &&
                    (zombie.getPosition().getX() >= SNORKEL_X_LIMIT)) {
                zombie.getZombieStats().getAttributes().replace("submarine", "on");
            }
        }
        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            for (Ability ability : plant.getOriginalAbilities()) {
                ability.executeAbility(zombie);
            }
        }
    }
}
