package src.Model.PlantsAndZombies.Abilities;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.Sun.Sun;

import java.util.List;

public class ProducingSun implements Ability {
    private static GamePlay GAME = GamePlayMenu.getGamePlay();

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
        return this.sun;
    }
}
