package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class MeleeAttacking implements Ability {
    private static Random RANDOM = new Random();
    private GamePlay GAME = GamePlay.activeInstance;


    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> tags = plant.getPlantStats().getTags();
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant, tags);
            }
            return;
        }

        if (tags.contains("insta-kill")) {
            if (GAME.getGameZombies().isEmpty()) {
                return;
            }

            int randomIndex = RANDOM.nextInt(GAME.getGameZombies().size());
            Zombie target = GAME.getGameZombies().get(randomIndex);
            target.setCurrentHP(0);
            plant.setLastActionTime(GAME.getTotalTimePassed());
        } else if (tags.contains("wramp-up")) {
            handleWramp_Up(plant);
        } else {
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");

            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();

            if (tags.contains("AoE")) {
                int range = (int) plant.getPlantStats().getAttributes().get("range");
                rangeDamage(plantRow, plantColumn, range, damage);
            } else {
                int frontAndBackRange = (int) plant.getPlantStats().getAttributes().get("front_range");
                for (int i = -frontAndBackRange; i <= frontAndBackRange; i++) {
                    Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow);
                    if (tile == null) {
                        continue;
                    }
                    takeDamage(plant, tile, damage);
                }
            }
        }
    }

    private void takeDamage(BattlePlant plant, Tile tile, int damage) {
        if (tile.getZombies() != null) {
            plant.setStatus("action");
        }

        if(GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
            GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
            GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)){
            if((!tile.isArable()) && (tile.getHP() > 0)){
                tile.setHP(tile.getHP() - damage);
            }
        }


        for (Zombie zombie : tile.getZombies()) {
            if (zombie.getCurrentHP() > 0) {
                zombie.takeDamage(plant, damage);
            }
        }
    }

    private void takeDamage(Tile tile, int damage) {
        for (Zombie zombie : tile.getZombies()) {
            zombie.setCurrentHP(zombie.getCurrentHP() - damage);
        }
    }

    private void rangeDamage(int plantRow, int plantColumn, int range, int damage) {
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow + j);
                if (tile == null) {
                    continue;
                }
                takeDamage(tile, damage);
            }
        }
    }

    private void rangeDamage(Position position, int range, int damage) {
        Position plantRowAndColumn = Position.getRowAndColumn(position);
        int plantRow = (int) plantRowAndColumn.getY();
        int plantColumn = (int) plantRowAndColumn.getX();

        rangeDamage(plantRow, plantColumn, range, damage);
    }

    private void handleWramp_Up(BattlePlant plant) {
        double plantTime = plant.getPlantTime();
        double currentTime = GAME.getTotalTimePassed();
        double differenceTime = currentTime - plantTime;

        ArrayList<Integer> timeStages = (ArrayList<Integer>) plant.getPlantStats().getAttributes().get("growth_time");
        ArrayList<Integer> damages = (ArrayList<Integer>) plant.getPlantStats().getAttributes().get("damage_quantity");
        int damage = 0;
        if (differenceTime >= timeStages.get(1)) {
            damage = damages.get(2);
        } else if (differenceTime >= timeStages.get(0)) {
            damage = damages.get(1);
        } else {
            damage = damages.get(0);
        }
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        rangeDamage(plant.getPosition(), range, damage);

    }

    private boolean checkTime(BattlePlant plant) {
        double currentTime = GAME.getTotalTimePassed();
        double timeDifference = 10 *(currentTime - plant.getEffectedTime());
        timeDifference = Math.floor(timeDifference);
        timeDifference /= 10;
        if ((timeDifference % 0.8) == 0) {//every 0.8 second, melee attackers execute their special ability
            return true;
        }
        return false;
    }

    private void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        Map<String, Object> plantFoodEffect = plant.getPlantStats().getPlantFoodEffect();
        if (tags.contains("insta-kill")) {
            int number = (int) plantFoodEffect.get("number");
            for (int i = 0; i < number; i++) {
                int randomIndex = RANDOM.nextInt(GAME.getGameZombies().size());
                Zombie target = GAME.getGameZombies().get(randomIndex);
                target.setCurrentHP(0);
            }
            return;
        }

        int damage = (int) plantFoodEffect.get("damage");
        if (plantFoodEffect.containsKey("range")) {
            int range = (int) plantFoodEffect.get("range");
            rangeDamage(plant.getPosition(), range, damage);
            return;
        }
        if (tags.contains("wramp-up")) {
            handleWramp_Up(plant);
            return;
        }
    }
}
