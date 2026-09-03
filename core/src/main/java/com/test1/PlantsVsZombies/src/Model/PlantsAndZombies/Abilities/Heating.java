package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.Tile;

public class Heating implements Ability {
    private static final int ICE_MELTING_DAMAGE = 6;
    private final GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

        rangeHeat(plant, plant.getRow(), plant.getColumn());

    }

    private void rangeHeat(BattlePlant plant, int row, int column) {
        int range = 1;

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                Tile tile = GAME.getTileByPosition(column + i, row + j);
                if (tile == null) {
                    continue;
                }

                for (BattlePlant plant1 : tile.getPlants()) {
                    if (plant1.isFrozen()) {
                        plant1.takeIceDamage(ICE_MELTING_DAMAGE);
                    }
                }
            }
        }
    }
}
