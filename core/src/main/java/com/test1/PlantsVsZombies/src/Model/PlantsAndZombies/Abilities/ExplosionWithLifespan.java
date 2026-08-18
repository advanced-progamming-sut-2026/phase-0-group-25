package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;

public class ExplosionWithLifespan implements Ability {
    private GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> tags = plant.getPlantStats().getTags();

        if (tags.contains("insta-kill")) {
            handleMakingArablePlants(plant, tags);
        }
        if (tags.contains("shroom")) {
            handleShroomPlants(plant, tags);
            return;
        }

        if (tags.contains("fire")) {
            fireLine(plant);
            return;
        }

        if (tags.contains("AoE")) {
            AoEDamage(plant);
            return;
        }
    }

    private void handleShroomPlants(BattlePlant plant, ArrayList<String> tags) {
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        if (tags.contains("Ice")) {
            for (Zombie zombie : GAME.getGameZombies()) {
                int frozenTime = (int) plant.getPlantStats().getAttributes().get("freezeTime");
                zombie.freeze(frozenTime);
                zombie.takeDamage(plant, damage);
            }
        } else {
            for (Tile tile : GAME.getTiles()) {
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(plant, damage);
                }
            }
            //todo:
            Tile tile = GAME.getTileByPosition(plant.getColumn(), plant.getRow());
            tile.setArable(false);
        }
    }

    private void fireLine(BattlePlant plant) {
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");

        int row = plant.getRow();
        for (int i = 0; i < 9; i++) {
            //todo
            Tile tile = GAME.getTileByPosition(i, row);
            for (Zombie zombie : tile.getZombies()) {
                zombie.unfreeze();
                zombie.takeDamage(plant, damage);
            }
            for (BattlePlant battlePlant : tile.getPlants()) {
                battlePlant.setFrozen(false);
            }
        }
    }

    private void AoEDamage(BattlePlant plant) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow + j);
                if (tile == null) {
                    continue;
                }
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(plant, damage);
                }
            }
        }

        if (plant.getPlantStats().getTags().contains("shoot")) {
            int bounce = (int) plant.getPlantStats().getAttributes().get("bounce");
            for (int i = 0; i < bounce; i++) {
                //todo
                Projectile projectile = new Projectile(plant, 50, 0, plant.getPosition(), 10, 1);
                GAME.getProjectiles().add(projectile);
            }
        }
    }

    private void handleMakingArablePlants(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("fire")) {
            int row = plant.getRow();
            int column = plant.getColumn();
            if (plant.getPlantStats().getAttributes().containsKey("range")) {
                rangeMelt(plant);
            } else {
                Tile tile = GAME.getTileByPosition(column, row);
                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(false);
                }
            }

        } else if (tags.contains("Ice")) {
            for (Zombie zombie : GAME.getGameZombies()) {
                zombie.freeze(2);
            }
        } else {
            for (Tile tile : GAME.getTiles()) {
                if ((GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT)) ||
                    ((GAME.getChapterType().equals(ChapterType.DARK_AGE)))) {
                    if (!tile.isArable()) {
                        tile.setArable(true);
                    }
                }
            }
        }

        if (plant.getPlantStats().getAttributes().containsKey("damage")) {
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");

            Tile tile = GAME.getTileByPosition(plant.getColumn(), plant.getRow());
            for (Zombie zombie : tile.getZombies()) {
                zombie.takeDamage(plant, damage);
            }
        }
    }

    private void rangeMelt(BattlePlant plant) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int plantRow = plant.getRow();
        int plantColumn = plant.getColumn();

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow + j);
                if (tile == null) {
                    continue;
                }

                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(false);
                }
            }
        }
    }
}
