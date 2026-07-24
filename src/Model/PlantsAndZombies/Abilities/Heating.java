package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

public class Heating implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

        rangeHeat(plant, plant.getRow(), plant.getColumn());

    }

    private void rangeHeat(BattlePlant plant, int row, int column) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int damage = 6;

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                //todo
                Tile tile = game.getTile();
                for (BattlePlant plant1 : tile.getPlants()) {
                    if (plant.isFrozen()) {
                        plant1.takeIceDamage(damage);
                    }
                }
            }
        }
    }
}
