package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.*;
import src.Model.Tile;

import java.util.Random;

public class Throwing implements Ability {
    private boolean isActivated = false;

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;
        Random random = new Random();

        if (zombie.getZombieStats().getName().equals("TOMB_RAISER")) {
            if (this.isActivated) {
                for (int i = 0; i < 2; i++) {
                    int row = random.nextInt(5) + 1;
                    int column = random.nextInt(9) + 1;
                    Tile tile = Tile.getTile(row, column); //todo: writing a function which returns a tile with specified row & column
                    //todo: function whose job is to make tile tomb
                    tile.setTomb();
                }
                this.isActivated = false;
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= 4) {
                    this.isActivated = true;
                }
            }
        } else if (zombie.getZombieStats().getName().equals("HUNTER")) {
            if (this.isActivated) {
                //todo: getter of plants
                Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
                int column = (int) zombieRowAndColumn.getX();
                int row = (int) zombieRowAndColumn.getY();
                double distance = 99999;
                BattlePlant battlePlant = null;

                for (int i = 0; i < 9; i++) {
                    Tile tile = ; //todo: related function
                    for (BattlePlant plant : tile.getPlants()) {
                        double tempDistance = plant.getPosition().distance(zombie.getPosition());
                        if (tempDistance < distance) {
                            distance = tempDistance;
                            battlePlant = plant;
                        }
                    }
                }
                if (battlePlant != null) {
                    battlePlant.setIceTime(battlePlant.getIceTime() + 1);
                }
                this.isActivated = false;
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= 2) {
                    this.isActivated = true;
                }
            }
        } else if (zombie.getZombieStats().getName().equals("OCTOPUS")) {
            if (this.isActivated) {
                //todo: getter of plants
                Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
                int column = (int) zombieRowAndColumn.getX();
                int row = (int) zombieRowAndColumn.getY();
                double distance = 99999;
                BattlePlant battlePlant = null;

                for (int i = 0; i < 9; i++) {
                    Tile tile = ; //todo: related function
                    for (BattlePlant plant : tile.getPlants()) {
                        double tempDistance = plant.getPosition().distance(zombie.getPosition());
                        if (tempDistance < distance) {
                            distance = tempDistance;
                            battlePlant = plant;
                        }
                    }
                }
                if (battlePlant != null) {
                    battlePlant.setOctopusated(true);
                }
                this.isActivated = false;
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= 2) {
                    this.isActivated = true;
                }
            }
        }
    }
}
