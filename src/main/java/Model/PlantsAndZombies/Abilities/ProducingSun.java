package Model.PlantsAndZombies.Abilities;

import Model.GameEngine;
import Model.PlantsAndZombies.BattlePlant;
import Model.PlantsAndZombies.Entity;
import Model.Sun.Sun;

import java.util.*;

public class ProducingSun implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        int numberOfSun = 0;

        Object sunAttributes = plant.getPlantStats().getAttributes().get("sun_quantity");
        Object timeAttributes = plant.getPlantStats().getAttributes().get("growth_time");


        if (sunAttributes instanceof List) {
            List<Integer> productionStages = (List<Integer>) sunAttributes;
            List<Integer> timeStages = (List<Integer>) timeAttributes;

            if (timeStages.get(1) <= plant.getTimeElapsedAsAlive()) {
                numberOfSun = productionStages.get(2);
            } else if (timeStages.get(0) <= plant.getTimeElapsedAsAlive()) {
                numberOfSun = productionStages.get(1);
            } else {
                numberOfSun = productionStages.get(0);
            }
        } else {
            numberOfSun = plant.getPlantStats().getAttributes().get("sun_quantity");
        }

        Sun producedSun = new Sun(numberOfSun, plant.getPosition());

        //currentUser.addSun(numberOfSun);
    }

}
