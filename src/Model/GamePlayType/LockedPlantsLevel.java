package Model.GamePlayType;

import java.util.ArrayList;

public class LockedPlantsLevel extends GamePlay {
    private ArrayList<Model.PlantsAndZombies.Plant> myPlants;

    @Override
    public void update() {}
    public ArrayList<Model.PlantsAndZombies.Plant> getMyPlants() {
        return myPlants;
    }
}
