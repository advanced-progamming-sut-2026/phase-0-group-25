package Model.GamePlayType;

import Model.PlantsAndZombies.BattlePlant;

import java.util.ArrayList;

public class DeadLine extends GamePlay {
    private ArrayList<BattlePlant> myPlants;

    @Override
    public void update() {}
    public void checkCrossingDeadline() {}
    public ArrayList<BattlePlant> getMyPlants() {
        return myPlants;
    }
}
