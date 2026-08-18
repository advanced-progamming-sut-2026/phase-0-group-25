package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.Random;

public class Explosion implements Ability {
    private static Random RANDOM = new Random();
    private GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        Zombie attacker = (Zombie) entity;
        BattlePlant plant = (BattlePlant) attacker.getRival();
        ArrayList<String> tags = plant.getPlantStats().getTags();
        if (plant.isEffected()) {
            plantFoodEffect(attacker, plant, tags);
        }

        if (isNotArmored(plant)) {
            return;
        }

        if (tags.contains("Ice")) {
            int frozenTime = (int) plant.getPlantStats().getAttributes().get("freezeTime");
            attacker.freeze(frozenTime);
        }

        if (tags.contains("Water")) {
            int number = (int) plant.getPlantStats().getAttributes().get("number");
            handleWaterPlant(attacker, plant, number);
        }

        if (tags.contains("AoE")) {
            AoEDamage(plant, plant.getRow(), plant.getColumn());
        }

        if (!plant.getName().equals(PlantType.SQUASH.getName()) &&
            !plant.getName().equals(PlantType.TANGLE_KELP.getName())) {
            plant.setCurrentHP(0);
        }

        if (plant.getPlantStats().getAttributes().containsKey("damage")) {
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            attacker.takeDamage(plant, damage);
        }

    }

    private boolean isNotArmored(BattlePlant plant) {
        if (plant.getPlantStats().getTags().contains("charge")) {
            int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
            if ((GAME.getTotalTimePassed() - plant.getPlantTime()) < armTime) {
                return true;
            }
            return false;
        }

        return false;
    }

    private void plantFoodEffect(Zombie attacker, BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("Ice")) {
            for (Zombie zombie : GAME.getGameZombies()) {
                int frozenTime = (int) plant.getPlantStats().getAttributes().get("freezeTime");
                zombie.freeze(frozenTime);
            }
            return;
        }
        if (tags.contains("Water")) {
            int number = (int) plant.getPlantStats().getPlantFoodEffect().get("number");
            handleWaterPlant(attacker, plant, number);
            return;
        }

        int number = (int) plant.getPlantStats().getPlantFoodEffect().get("number");

        if (tags.contains("charge")) {
            int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
            plant.setPlantTime(plant.getPlantTime() - armTime);

            for (int i = 0; i < number; i++) {
                int randomRow = RANDOM.nextInt(5) + 1;
                int randomColumn = RANDOM.nextInt(9) + 1;

                if (tags.contains("AoE")) {
                    AoEDamage(plant, randomRow, randomColumn);
                }

                Tile tile = GAME.getTileByPosition(randomColumn, randomRow);
                if (tile == null) {
                    continue;
                }
                int damage = (int) plant.getPlantStats().getAttributes().get("damage");

                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(plant, damage);
                }
            }
            return;
        }

        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        for (int i = 0; i < number; i++) {
            int randomIndex = RANDOM.nextInt(GAME.getGameZombies().size());
            GAME.getGameZombies().get(randomIndex).takeDamage(plant, damage);
        }
    }

    private void handleWaterPlant(Zombie attacker, BattlePlant plant, int number) {
        attacker.setCurrentHP(0);
        outer:
        for (Tile tile : GAME.getTiles()) {
            if (!tile.isArable()) {
                for (Zombie zombie : tile.getZombies()) {
                    zombie.setCurrentHP(0);
                    number -= 1;
                    if (number <= 0) {
                        break outer;
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
    }
}
