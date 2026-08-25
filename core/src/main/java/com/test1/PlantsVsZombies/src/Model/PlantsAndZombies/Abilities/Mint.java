package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;

public class Mint implements Ability {
    private GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        String category = plant.getPlantStats().getCategory();
        int duration = (int) plant.getPlantStats().getAttributes().get("duration");

        for (BattlePlant plant1 : GAME.getGamePlants()) {
            if (plant1.getPlantStats().getCategory().equals(category)) {
                plant1.setEffected(true, 7);
            }
        }

        plant.setLastActionTime(GAME.getTotalTimePassed());
    }
}
