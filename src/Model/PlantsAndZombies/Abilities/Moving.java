package src.Model.PlantsAndZombies.Abilities;

import src.Enums.Status;
import src.Model.PlantsAndZombies.*;
import src.Model.Tile;


public class Moving implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        double velocity = zombie.getCurrentVelocity();
        double differenceX = velocity * 0.1;

        double zombieFinalPositionX = zombie.getPosition().getX() - differenceX;
        Position newPosition = new Position(zombieFinalPositionX, zombie.getPosition().getY());
        zombie.setPosition(newPosition);



        if (zombie.getZombieStats().getName().equals("EXPLORER")) {
            if (zombie.getZombieStats().getAttributes().get("torch").equals("on")) {
                Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
                int zombieColumn = (int) zombieRowAndColumn.getX();
                int zombieRow = (int) zombieRowAndColumn.getY();

                Tile tile = ; //todo: function for finding the proper tile for EXPLORER_ZOMBIE

                BattlePlant plant = tile.getPlant();
                zombie.setRival(plant);

                return;
            }
        }

        //todo: getter of plants on game board
        for (BattlePlant plant : game.getPlants()) {
            Position plantPosition = plant.getPosition();

            if (zombie.getPosition().equals(plantPosition)) {
                zombie.setCurrentVelocity(0);
                zombie.setRival(plant);
                zombie.setStatus(Status.EATING);
                if (zombie.getZombieStats().getName().equals("EXPLORER")) {
                    if (plant.getPlantStats().getTags().contains("ice")) {
                        zombie.getZombieStats().getAttributes().replace("torch", "off");
                    } else if (plant.getPlantStats().getTags().contains("fire")) {
                        zombie.getZombieStats().getAttributes().replace("torch", "on");
                    }
                }
                return;
            }

        }
        if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
            //todo: getter of zombies on game board
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
