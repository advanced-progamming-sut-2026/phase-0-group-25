package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;


public class MeleeAttacking implements Ability {
    private static Random RANDOM = new Random();

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
            //todo
            int randomIndex = RANDOM.nextInt(game.getZombies());
            Zombie target = game.getZombies().get(randomIndex);
            target.setCurrentHP(0);
        } else if (tags.contains("wramp-up")) {
            handleWramp_Up(plant);
        } else {
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition());
            int plantRow = (int) plantRowAndColumn.getY();
            int plantColumn = (int) plantRowAndColumn.getX();

            if (tags.contains("AoE")) {
                int range = (int) plant.getPlantStats().getAttributes().get("range");
                rangeDamage(plantRow, plantColumn, range, damage);
            } else {
                int frontAndBackRange = (int) plant.getPlantStats().getAttributes().get("front-range");
                for (int i = 0; i < frontAndBackRange; i++) {
                    Tile tile = Tile.getTile();//todo:
                    takeDamage(tile, damage);
                }
            }
        }
    }

    private void takeDamage(Tile tile, int damage) {
        //todo:
        for (Zombie zombie : tile.getZombies()) {
            zombie.setCurrentHP(zombie.getCurrentHP() - damage);
        }
    }

    private void rangeDamage(int plantRow, int plantColumn, int range, int damage) {
        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                //todo
                Tile tile = Tile.getTile();
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
        double currentTime = game.getCurrentTime();//todo
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
        //todo:
        double currentTime = game.getCurrentTime();
        double timeDifference = (currentTime - plant.getEffectedTime());
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
                //todo
                int randomIndex = RANDOM.nextInt(game.getZombies());
                Zombie target = game.getZombies().get(randomIndex);
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
