package src.Model.PlantsAndZombies.Abilities;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.Tile;

public class Heating implements Ability {
    private static int ICE_MELTING_DAMAGE = 6;
    private GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

        rangeHeat(plant, plant.getRow(), plant.getColumn());

    }

    private void rangeHeat(BattlePlant plant, int row, int column) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(column + i, row + j);
                for (BattlePlant plant1 : tile.getPlants()) {
                    if (plant.isFrozen()) {
                        plant1.takeIceDamage(ICE_MELTING_DAMAGE);
                    }
                }
            }
        }
    }
}
