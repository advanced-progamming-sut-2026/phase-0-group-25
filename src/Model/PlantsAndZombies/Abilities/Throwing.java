package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Throwing implements Ability {
    private boolean isActivated = false;
    private static int TILE_X_LENGTH = 200;
    private static int TOMB_RAISER_ACTION_INTERVAL = 4;
    private static int HUNTER_ACTION_INTERVAL = 2;
    private static int OCTOPUS_ACTION_INTERVAL = 4;
    private static int FISHERMAN_ACTION_INTERVAL = 3;
    private static int KING_ACTION_INTERVAL = 5;
    private static Random RANDOM = new Random();

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

                afterAbility(zombie);
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= TOMB_RAISER_ACTION_INTERVAL) {
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

                afterAbility(zombie);
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= HUNTER_ACTION_INTERVAL) {
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

                afterAbility(zombie);
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= OCTOPUS_ACTION_INTERVAL) {
                    this.isActivated = true;
                }
            }
        } else if (zombie.getZombieStats().getName().equals("FISHERMAN")) {
            if (this.isActivated) {
                Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
                int zombieColumn = (int) zombieRowAndColumn.getX();
                int zombieRow = (int) zombieRowAndColumn.getY();

                ArrayList<BattlePlant> plantsInSameRow = new ArrayList<>();
                for (int i = 0; i < 9; i++) {
                    Tile tile = Tile.getTile();//todo:
                    //todo:
                    for (BattlePlant plant : tile.getPlants()) {
                        plantsInSameRow.add(plant);
                    }

                    BattlePlant targetPlant = null;
                    if (!plantsInSameRow.isEmpty()) {
                        int randomIndex = RANDOM.nextInt(plantsInSameRow.size());
                        targetPlant = plantsInSameRow.get(randomIndex);
                    }

                    if (targetPlant != null) {
                        Tile targetRightTile = Tile.getTile();//todo:
                        //todo:
                        if (targetRightTile.isArable()) {
                            targetPlant.setPosition(new Position(
                                    targetPlant.getPosition().getX() + TILE_X_LENGTH,
                                    targetPlant.getPosition().getY()));
                        } else {
                            targetPlant.setCurrentHP(0);
                        }
                    }
                }

                afterAbility(zombie);
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= FISHERMAN_ACTION_INTERVAL) {
                    this.isActivated = true;
                }
            }
        } if (zombie.getZombieStats().getName().equals("KING")) {
            if (this.isActivated) {
                ArrayList<Zombie> ordinaryZombiesInRange = findOrdinaryZombiesNearKing(zombie);

                if (!ordinaryZombiesInRange.isEmpty()) {
                    int randomIndex = random.nextInt(ordinaryZombiesInRange.size());
                    Zombie newKnight = ordinaryZombiesInRange.get(randomIndex);

                    newKnight.makeKnight();
                }
                afterAbility(zombie);
            } else {
                if ((game.getCurrentTime() - zombie.getLastActionTime()) >= KING_ACTION_INTERVAL) {
                    this.isActivated = true;
                }
            }
        }
    }

    public void afterAbility(Zombie zombie) {
        //todo:
        zombie.setLastActionTime(game.getCurrentTime());
        this.isActivated = false;
    }

    public ArrayList<Zombie> findOrdinaryZombiesNearKing(Zombie zombie) {
        Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
        int zombieColumn = (int) zombieRowAndColumn.getX();
        int zombieRow = (int) zombieRowAndColumn.getY();

        ArrayList<Zombie> properZombie = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Tile tile = Tile.getTile();//todo: two near column of KING_ZOMBIE
            for (Zombie zombieInTile : tile.getZombies()) {
                if (zombie.getZombieStats().getName().equals("DEFAULT")) {
                    properZombie.add(zombieInTile);
                }
            }
        }

        return properZombie;
    }
}
