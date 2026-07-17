package src.Model.GamePlayType;

import java.util.ArrayList;

public class DeadLine extends GamePlay {
    private ArrayList<src.Model.PlantsAndZombies.BattlePlant> myPlants;

    @Override
    public void update() {}
    public void checkCrossingDeadline() {}
    public ArrayList<src.Model.PlantsAndZombies.BattlePlant> getMyPlants() {
        return myPlants;
    }
}
