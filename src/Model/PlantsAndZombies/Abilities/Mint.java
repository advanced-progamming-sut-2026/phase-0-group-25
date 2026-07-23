package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Plant;
import tools.jackson.databind.DeserializationFeature;

public class Mint implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        String category = plant.getPlantStats().getCategory();
        int duration = (int) plant.getPlantStats().getAttributes().get("duration");

        //todo
        for (BattlePlant plant1 : game.getPlants()) {
            if (plant1.getPlantStats().getCategory().equals(category)) {
                plant1.setEffected(true, duration);
            }
        }
    }
}
