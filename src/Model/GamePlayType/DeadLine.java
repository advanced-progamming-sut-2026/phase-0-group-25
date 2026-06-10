package Model.GamePlayType;

import java.util.ArrayList;

public class DeadLine extends GamePlay {
    private ArrayList<Model.PlantsAndZombies.Plant> myPlants;

    @Override
    public void update() {}
    public void checkCrossingDeadline() {}
    public ArrayList<Model.PlantsAndZombies.Plant> getMyPlants() {
        return myPlants;
    }
}
