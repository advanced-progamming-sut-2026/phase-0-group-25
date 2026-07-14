package src.Model.GamePlayType;

import src.Model.PlantsAndZombies.*;
import java.util.ArrayList;

public class Simple extends GamePlay {
    private ArrayList<Plant> myPlants;

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        sunMaker();

        for (Plant plant : gamePlants) {
            if(plant.isAlive()) plant.update();
        }

        for (Zombie zombie : gameZombies) {
            if(zombie.isAlive()) zombie.update();
        }
    }

}
