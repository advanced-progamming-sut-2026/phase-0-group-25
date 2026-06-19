package src.Model.PlantsAndZombies.Abilities;

import src.Model.GameEngine;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.Sun.Sun;

public class ProducingSun implements Ability {
    private BattlePlant plant;

    @Override
    public void executeAbility() {
        int numberOfSun = plant.getPlantStats().getAttributes().get("sun_quantity");

        Sun producedSun = new Sun(numberOfSun, plant.getPosition());

        //currentUser.addSun(numberOfSun);
    }
}
