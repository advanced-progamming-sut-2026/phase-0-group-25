package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Throwing implements Ability {
    private static int TILE_X_LENGTH = 200;
    private static int TOMB_RAISER_ACTION_INTERVAL = 4;
    private static int HUNTER_ACTION_INTERVAL = 2;
    private static int OCTOPUS_ACTION_INTERVAL = 4;
    private static int FISHERMAN_ACTION_INTERVAL = 3;
    private static int KING_ACTION_INTERVAL = 5;
    private static Random RANDOM = new Random();

    private GamePlay GAME = GamePlayMenu.getGamePlay();

    private boolean isActivated = false;

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;
        Random random = new Random();

        if (zombie.getZombieStats().getName().equals("TOMB_RAISER")) {
            handleTombRaiserZombie(zombie);
        } else if (zombie.getZombieStats().getName().equals("HUNTER")) {
            if (this.isActivated) {
                BattlePlant target = findTargetForHunterAndOctopus(zombie);

                if (target != null) {
                    target.setIceTime(target.getIceTime() + 1);
                }

                afterAbility(zombie);
            } else {
                if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= HUNTER_ACTION_INTERVAL) {
                    this.isActivated = true;
                }
            }
        } else if (zombie.getZombieStats().getName().equals("OCTOPUS")) {
            if (this.isActivated) {

                BattlePlant target = findTargetForHunterAndOctopus(zombie);

                if (target != null) {
                    target.setIceTime(3);
                }
                afterAbility(zombie);
            } else {
                if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= OCTOPUS_ACTION_INTERVAL) {
                    this.isActivated = true;
                }
            }
        } else if (zombie.getZombieStats().getName().equals("FISHERMAN")) {
            handleFishermanZombie(zombie);
        } else if (zombie.getZombieStats().getName().equals("KING")) {
            handleKingZombie(zombie);
        }
    }

    private void handleTombRaiserZombie(Zombie zombie) {
        if (this.isActivated) {
            for (int i = 0; i < 2; i++) {
                int row = RANDOM.nextInt(5) + 1;
                int column = RANDOM.nextInt(9) + 1;
                Tile tile = GAME.getTileByPosition(column, row);
                tile.setArable(false);
            }

            afterAbility(zombie);
        } else {
            if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= TOMB_RAISER_ACTION_INTERVAL) {
                this.isActivated = true;
            }
        }
    }

    private BattlePlant findTargetForHunterAndOctopus(Zombie zombie) {
        double distance = 99999;
        BattlePlant battlePlant = null;

        for (int i = 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, zombie.getRow());
            for (BattlePlant plant : tile.getPlants()) {
                double tempDistance = plant.getPosition().distance(zombie.getPosition());
                if (tempDistance < distance) {
                    distance = tempDistance;
                    battlePlant = plant;
                }
            }
        }

        return battlePlant;
    }

    private void afterAbility(Zombie zombie) {
        zombie.setLastActionTime(GAME.getTotalTimePassed());
        this.isActivated = false;
    }

    private ArrayList<Zombie> findOrdinaryZombiesNearKing(Zombie zombie) {
        int zombieColumn = zombie.getColumn();
        int zombieRow = zombie.getRow();

        ArrayList<Zombie> properZombie = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            Tile tile = GAME.getTileByPosition(zombieColumn, zombieRow + i);
            if (tile == null) {
                continue;
            }

            for (Zombie zombieInTile : tile.getZombies()) {
                if (zombie.getZombieStats().getName().equals("DEFAULT")) {
                    properZombie.add(zombieInTile);
                }
            }
        }

        return properZombie;
    }

    private void handleKingZombie(Zombie zombie) {
        if (this.isActivated) {
            ArrayList<Zombie> ordinaryZombiesInRange = findOrdinaryZombiesNearKing(zombie);

            if (!ordinaryZombiesInRange.isEmpty()) {
                int randomIndex = RANDOM.nextInt(ordinaryZombiesInRange.size());
                Zombie newKnight = ordinaryZombiesInRange.get(randomIndex);

                newKnight.makeKnight();
            }
            afterAbility(zombie);
        } else {
            if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= KING_ACTION_INTERVAL) {
                this.isActivated = true;
            }
        }
    }

    private void handleFishermanZombie(Zombie zombie) {
        if (this.isActivated) {
            ArrayList<BattlePlant> plantsInSameRow = getPlantInSameRow(zombie);

            BattlePlant targetPlant = null;
            if (!plantsInSameRow.isEmpty()) {
                int randomIndex = RANDOM.nextInt(plantsInSameRow.size());
                targetPlant = plantsInSameRow.get(randomIndex);
            }

            if (targetPlant != null) {
                Tile targetRightTile = GAME.getTileByPosition(targetPlant.getColumn() + 1,
                        targetPlant.getRow());

                if (isVoid(targetRightTile)) {
                    targetPlant.setPosition(new Position(
                            targetPlant.getPosition().getX() + TILE_X_LENGTH,
                            targetPlant.getPosition().getY()));
                } else {
                    targetPlant.setCurrentHP(0);
                }
            }
            afterAbility(zombie);
        } else {
            if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= FISHERMAN_ACTION_INTERVAL) {
                this.isActivated = true;
            }
        }
    }

    private ArrayList<BattlePlant> getPlantInSameRow(Zombie zombie) {
        int zombieRow = zombie.getRow();

        ArrayList<BattlePlant> plantsInSameRow = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            Tile tile = GAME.getTileByPosition(i, zombieRow);

            for (BattlePlant plant : tile.getPlants()) {
                plantsInSameRow.add(plant);
            }
        }

        return plantsInSameRow;
    }

    private boolean isVoid(Tile tile) {
        if (tile.isArable()) {
            if (tile.getPlants().isEmpty()) {
                return true;
            }
            return false;
        }
        return false;
    }
}
