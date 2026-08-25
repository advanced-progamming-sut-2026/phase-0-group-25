package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;

import java.util.List;

public class ProducingSun implements Ability {
    private GamePlay GAME = GamePlay.activeInstance;

    private boolean isCollected = false;
    private boolean isProduced = false;
    private Sun sun;

    @Override
    public void executeAbility(Entity entity) {

        BattlePlant plant = (BattlePlant) entity;

        if (this.isProduced && !this.isCollected) {
            return;
        }

        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }
        int numberOfSun = 0;


        if (plant.getPlantStats().getTags().contains("wramp-up")) {
            List<Integer> productionStages = (List<Integer>) plant.getPlantStats().getAttributes().get("sun_quantity");
            List<Integer> timeStages = (List<Integer>) plant.getPlantStats().getAttributes().get("growth_time");


            double plantLifespanTime = (GAME.getTotalTimePassed() - plant.getPlantTime());

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
        double currentTime = GAME.getTotalTimePassed();
        double timeDifference = 10 * (currentTime - plant.getEffectedTime());
        timeDifference = Math.floor(timeDifference);
        timeDifference /= 10;
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
        GAME.getActiveSuns().add(this.sun);

        this.isProduced = true;

        System.out.println("plant " + plant.getPlantStats().getName() +
            " produced a sun at (" + plant.getColumn() + ", "
            + plant.getRow() + ")");
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public boolean isProduced() {
        return isProduced;
    }

    public void setProduced(boolean produced) {
        isProduced = produced;
    }

    public boolean isReadyToCollect() {
        return ((this.isProduced) && !(this.isCollected));
    }

    public Sun getSun() {
        return this.sun;
    }
}
