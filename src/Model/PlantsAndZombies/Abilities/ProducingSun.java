package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.Sun.Sun;

import java.util.*;

public class ProducingSun implements Ability {
    private boolean isCollected = false;
    private boolean isProduced = false;
    private Sun sun;

    @Override
    public void executeAbility(Entity entity) {
        if (this.isProduced && !this.isCollected) {
            return;
        }

        BattlePlant plant = (BattlePlant) entity;
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }
        int numberOfSun = 0;

        Object sunAttributes = plant.getPlantStats().getAttributes().get("sun_quantity");
        Object timeAttributes = plant.getPlantStats().getAttributes().get("growth_time");

        if (sunAttributes instanceof List) {
            List<Integer> productionStages = (List<Integer>) sunAttributes;
            List<Integer> timeStages = (List<Integer>) timeAttributes;

            //todo
            double plantLifespanTime = (game.getCurrentTime() - plant.getPlantTime());

            if (timeStages.get(1) <= plantLifespanTime) {
                numberOfSun = productionStages.get(2);
            } else if (timeStages.get(0) <= plantLifespanTime) {
                numberOfSun = productionStages.get(1);
            } else {
                numberOfSun = productionStages.get(0);
            }
        } else {
            numberOfSun = (int) plant.getPlantStats().getAttributes().get("sun_quantity");
        }

        makeSun(numberOfSun, plant);
    }

    private boolean checkTime(BattlePlant plant) {
        //todo:
        double currentTime = game.getCurrentTime();
        double timeDifference = (currentTime - plant.getEffectedTime());
        if ((timeDifference % 1) == 0) {//every second, sun producers execute their special ability
            return true;
        }
        return false;
    }

    private void plantFoodEffect(BattlePlant plant) {
        int numberOfSun = (int) plant.getPlantStats().getPlantFoodEffect().get("sun_quantity");

        if (plant.getPlantStats().getName().equals("SUN_SHROOM")) {
            plant.setPlantTime(-72);
        }
        makeSun(numberOfSun, plant);
    }

    private void makeSun(int numberOfSun, BattlePlant plant) {
        Sun producedSun = new Sun(numberOfSun, plant.getPosition());
        this.sun = producedSun;
        this.isProduced = true;

        System.out.println("plant + " + plant.getPlantStats().getName() +
                " produced a sun at (<" + plant.getColumn() + ">, <"
                + plant.getRow() + ">)");
    }

    public boolean isCollected() {
        return isCollected;
    }

    public boolean isProduced() {
        return isProduced;
    }

    public boolean isReadyToCollect() {
        return ((this.isProduced) && !(this.isCollected));
    }

    public Sun getSun() {
        if (this.isReadyToCollect()) {
            return sun;
        }
    }
}
