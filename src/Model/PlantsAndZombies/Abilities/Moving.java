package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.*;
import src.Model.Tile;


public class Moving implements Ability {
    private static int SNORKEL_X_LIMIT = 1420;
    private static int PIANO_ACTION_INTERVAL = 3;

    private boolean isActivated = true;

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
        } else if (zombie.getZombieStats().getName().equals("PIANO")) {
            //todo:
            if ((game.getCurrentTime() - zombie.getLastActionTime()) >= PIANO_ACTION_INTERVAL) {
                //todo:
                for (Zombie zombie1 : game.getZombies()) {
                    zombie1.changeRow();
                }
                //todo:
                zombie.setLastActionTime(game.getCurrentTime());
            }
        }

        //todo: getter of plants on game board
        for (BattlePlant plant : game.getPlants()) {
            Position plantPosition = plant.getPosition();

            if (zombie.getPosition().equals(plantPosition)) {
                zombie.setCurrentVelocity(0);
                zombie.setRival(plant);
                //todo: difference between eating and fatal damage
                zombie.setStatus(Status.EATING);

                if (zombie.getZombieStats().getName().equals("DODO")) {
                    if (isObstacle(plant)) {
                        //todo: activation of flying ability
                    }
                }

                if (zombie.getZombieStats().getName().equals("EXPLORER")) {
                    if (plant.getPlantStats().getTags().contains("ice")) {
                        zombie.getZombieStats().getAttributes().replace("torch", "off");
                    } else if (plant.getPlantStats().getTags().contains("fire")) {
                        zombie.getZombieStats().getAttributes().replace("torch", "on");
                    }
                } else if ((zombie.getZombieStats().getName().equals("SNORKEL")) &&
                        (zombie.getPosition().getX() < SNORKEL_X_LIMIT)) {
                    zombie.getZombieStats().getAttributes().replace("submarine", "off");
                }
                return;
            }

        }
        if (zombie.getZombieStats().getName().equals("ALL_STAR") &&
                (zombie.getZombieStats().getName().equals("TROGLOBITE")) &&
                (zombie.getZombieStats().getName().equals("ARCADE"))) {
            //todo: getter of zombies on game board
            for (Zombie zombie1 : game.getZombies()) {
                Position zombie1Position = zombie1.getPosition();

                if ((zombie.getPosition().equals(zombie1Position)) && (zombie1.isHypnotized())) {
                    zombie.setCurrentVelocity(0);
                    zombie.setRival(zombie1);
                    if ((zombie.getZombieStats().getName().equals("ARCADE")) &&
                            (zombie.getActiveArmors().isEmpty())) {
                        //todo: eating
                    }
                    // TODO: fatal damage
                    zombie.setStatus(Status.EATING);
                }
            }
        }


    }

    public boolean isObstacle(BattlePlant plant) {
        //todo:
    }
}
