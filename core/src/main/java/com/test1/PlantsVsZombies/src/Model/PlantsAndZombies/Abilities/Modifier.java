package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;

public class Modifier implements Ability {
    private GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> tags = plant.getPlantStats().getTags();
        if (plant.isEffected()) {
            plantFoodEffect(plant, tags);
            return;
        }

        if (tags.contains("fire")) {
            float HPRatio = (float) plant.getCurrentHP() / plant.getPlantStats().getBaseHP();
            if (HPRatio <= 0.1) {
                AoEDamage(plant, plant.getRow(), plant.getColumn());
                return;
            }
            //todo
            for (Projectile projectile : GAME.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setFiring(true);
                }
            }
        }


    }

    private void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("fire")) {
            //todo
            for (Projectile projectile : GAME.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setBlueFiring(true);
                }
            }
        }

        if (tags.contains("Water")) {
            if (GAME.getChapterType().equals(ChapterType.BIG_WAVE_BEACH)) {
                ArrayList<Tile> nonArableTiles = new ArrayList<>();
                for (Tile tile : GAME.getTiles()) {
                    if (!tile.isArable()) {
                        nonArableTiles.add(tile);
                    }
                }

                if (nonArableTiles.size() > 2) {

                } else {
                    for (Tile tile : nonArableTiles) {
                        BattlePlant newLilyPad;
                    }
                }
            }
        }
    }

    private void AoEDamage(BattlePlant plant, int row, int column) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(column + i, row + j);

                if (tile == null) {
                    continue;
                }
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(plant, damage);
                }
            }
        }

        plant.setCurrentHP(0);
        plant.setAlive(false);
    }
}
