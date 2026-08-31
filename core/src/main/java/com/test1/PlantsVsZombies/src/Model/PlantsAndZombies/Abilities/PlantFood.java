package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.PlantFactory;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import javax.management.openmbean.CompositeDataSupport;
import java.util.ArrayList;
import java.util.Random;

public class PlantFood {
    private GamePlay GAME = GamePlay.activeInstance;
    private static Random RANDOM = new Random();

    public void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            wallnutPlantFood(plant);
        } else {
            explosionPlantFood(plant, tags);
        }
    }

    private void explosionPlantFood(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("Ice")) {
            for (Zombie zombie : GAME.getGameZombies()) {
                int frozenTime = (int) plant.getPlantStats().getAttributes().get("freezeTime");
                zombie.freeze(frozenTime);
            }
            return;
        }
        if (tags.contains("Water")) {
            int number = (int) plant.getPlantStats().getPlantFoodEffect().get("number");
            handleWaterPlant(plant, number);
            return;
        }

        int number = (int) plant.getPlantStats().getPlantFoodEffect().get("number");

        if (tags.contains("charge")) {
            int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
            plant.setPlantTime(plant.getPlantTime() - armTime);

            for (int i = 0; i < number; i++) {
                int randomRow = RANDOM.nextInt(5) + 1;
                int randomColumn = RANDOM.nextInt(9) + 1;

                Tile tile = GAME.getTileByPosition(randomColumn, randomRow);
                BattlePlant newPlant = PlantFactory.createBattlePlant(plant.getName(), plant.getPlantStats().getLevel(),
                    new Position(1, 1));

                tile.addPlant(newPlant);

                /*
                if (tile.isArable() &&
                    tile.getPlants().isEmpty()) {
                    BattlePlant newPlant = PlantFactory.createBattlePlant(plant.getName(), plant.getPlantStats().getLevel(),
                        new Position(1, 1));
                    GAME.planting(newPlant, new Position(randomColumn, randomRow));
                    newPlant.setPlantTime(newPlant.getPlantTime() - armTime);
                }

                 */
            }
            return;
        }

        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        for (int i = 0; i < number; i++) {
            int randomIndex = RANDOM.nextInt(GAME.getGameZombies().size());
            GAME.getGameZombies().get(randomIndex).takeDamage(plant, damage);
        }
    }

    private void handleWaterPlant(BattlePlant plant, int number) {
        if (!GAME.getChapterType().equals(ChapterType.BIG_WAVE_BEACH)) {
            return;
        }
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

    private void wallnutPlantFood(BattlePlant plant) {

        if (!plant.getPlantStats().getTags().contains("move-zombies")) {
            int armor = (int) plant.getPlantStats().getPlantFoodEffect().get("armor");
            plant.setArmorHP(armor);

            plant.setCurrentHP(plant.getPlantStats().getBaseHP());
        } else {
            for (Zombie zombie : GAME.getGameZombies()) {
                zombie.changeRow();
            }
        }
    }
}
