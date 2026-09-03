package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Modifier implements Ability {
    private static final int X_RIGHT_LIMIT = 1860;
    private static final int Y_UP_LIMIT = 880;
    private static final int Y_DOWN_LIMIT = 130;
    private static final int X_LEFT_LIMIT = 490;
    private static final double X_TILE_LENGTH = 152.2;
    private static final int Y_TILE_LENGTH = 150;
    private final GamePlay GAME = GamePlay.activeInstance;

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
                if (tags.contains("AoE")) {
                    AoEDamage(plant, plant.getRow(), plant.getColumn());
                    return;
                }
            }
            for (Projectile projectile : GAME.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setFiring(true);
                }
            }
        }


    }

    private void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("fire")) {
            for (Projectile projectile : GAME.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setBlueFiring(true);
                }
            }
        }

        if (tags.contains("Water")) {
            double difference = GAME.getTotalTimePassed() - plant.getEffectedTime();
            if (Math.abs(difference - plant.getEffectedLifeSpan()) > 0.1) {
                return;
            }

            if (GAME.getChapterType().equals(ChapterType.BIG_WAVE_BEACH)) {
                ArrayList<Tile> nonArableTiles = new ArrayList<>();
                for (Tile tile : GAME.getTiles()) {
                    if (!tile.isArable() &&
                        tile.getPlants().isEmpty()) {
                        nonArableTiles.add(tile);
                    }
                }

                if (nonArableTiles.isEmpty()) {
                    return;
                }

                Random RANDOM = new Random();
                for (int i = 0; i < 1; i++) {
                    int randomTile = RANDOM.nextInt(nonArableTiles.size());
                    Tile target = nonArableTiles.get(randomTile);

                    Position newPosition = new Position(target.getPosition().getX(), target.getPosition().getY());
                    BattlePlant newLilyPad = GAME.plantFromPlantFood(plant, newPosition);
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
