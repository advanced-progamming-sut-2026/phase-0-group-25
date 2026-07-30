package src.Model.PlantsAndZombies.Abilities;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;

public class Mint implements Ability {
    private static GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        String category = plant.getPlantStats().getCategory();
        int duration = (int) plant.getPlantStats().getAttributes().get("duration");

        for (BattlePlant plant1 : GAME.getPlants()) {
            if (plant1.getPlantStats().getCategory().equals(category)) {
                plant1.setEffected(true, duration);
            }
        }
    }
}
