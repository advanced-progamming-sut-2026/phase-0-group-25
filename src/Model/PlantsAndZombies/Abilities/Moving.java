package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.Entity;
import src.Enums.Status;
import src.Model.PlantsAndZombies.*;


public class Moving implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        double velocity = zombie.getCurrentVelocity();
        double differenceX = velocity * 0.1;

        double zombieFinalPositionX = zombie.getPosition().getX() - differenceX;
        Position newPosition = new Position(zombieFinalPositionX, zombie.getPosition().getY());
        zombie.setPosition(newPosition);

        //todo: getter of plants on game board
        for (BattlePlant plant : game.getPlants()) {
            Position plantPosition = plant.getPosition();

            if (zombie.getPosition().equals(plantPosition)) {
                zombie.setCurrentVelocity(0);
                zombie.setRival(plant);
                zombie.setStatus(Status.EATING);
                return;
            }

        }
        if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
            for (Zombie zombie1 : game.getZombies()) {
                Position zombie1Position = zombie1.getPosition();

                if ((zombie.getPosition().equals(zombie1Position)) && (zombie1.isHypnotized())) {
                    zombie.setCurrentVelocity(0);
                    zombie.setRival(zombie1);
                    zombie.setStatus(Status.EATING);
                }

            }
        }
    }
}

