package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.ArrayList;


public class MeleeAttacking implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> plantTags = plant.getPlantStats().getTags();

        if (plantTags.contains("AoE")) {
            if (plantTags.contains("wramp-up")) {
                ArrayList<Integer> timeAttributes = (ArrayList<Integer>) (Object) plant.getPlantStats().getAttributes().get("growth_time");
                double plantAge = plant.getTimeElapsedAsAlive();

                ArrayList<Integer> damageAttributes = (ArrayList<Integer>) (Object) plant.getPlantStats().getAttributes().get("damage_quantity");
                int damage = 0;
                int range = plant.getPlantStats().getAttributes().get("range");

                if (timeAttributes.get(1) <= plant.getTimeElapsedAsAlive()) {
                    damage = damageAttributes.get(2);
                } else if (timeAttributes.get(0) <= plant.getTimeElapsedAsAlive()) {
                    damage = damageAttributes.get(1);
                } else {
                    damage = damageAttributes.get(0);
                }

                AoEDamageZombies(plant, range, damage);

            } else {
                int damage = plant.getPlantStats().getAttributes().get("damage");
                int range = plant.getPlantStats().getAttributes().get("range");

                AoEDamageZombies(plant, range, damage);
            }
        } else if (plantTags.contains("insta-kill")) {
            Zombie target = findNearestZombie();//todo: writing a function which can find the nearest zombie to chomper

            target.setCurrentHP(0);

            //todo: handling digest time
        } else {
            int damage = plant.getPlantStats().getAttributes().get("damage");
            int backRange = plant.getPlantStats().getAttributes().get("back_range");
            int frontRange = plant.getPlantStats().getAttributes().get("front_range");

            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(),
                    plant.getPosition().getY());
            int plantRow = (int) plantRowAndColumn.getX();
            int plantColumn = (int) plantRowAndColumn.getY();

            for (int i = 1; i <= backRange; i++) {
                damageZombies(plantRow, plantColumn - i, damage);
            }

            for (int i = 1; i <= frontRange; i++) {
                damageZombies(plantRow, plantColumn + i, damage);
            }
        }


    }

    public void AoEDamageZombies(BattlePlant plant, int range, int damage) {
        Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(),
                plant.getPosition().getY());
        int plantRow = (int) plantRowAndColumn.getX();
        int plantColumn = (int) plantRowAndColumn.getY();


        for (int i = (-1 * range); i <= range; i++) {
            for (int j = (-1 * range); j <= range; j++) {
                int row = plantRow + i;
                int column = plantColumn + j;

                damageZombies(row, column, damage);
            }
        }
    }

    public void damageZombies(int row, int column, int damage) {
        //todo: writing functions which can give us the proper tile with given row and column coordinates
        Tile tile = getTile();
        //todo: getter for zombies in one tile
        ArrayList<Zombie> zombies = tile.getZombies();
        for (Zombie zombie : zombies) {
            zombie.setCurrentHP(zombie.getCurrentHP() - damage);
        }
    }

}
