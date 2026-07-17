package Model.GamePlayType;

import Model.PlantsAndZombies.BattlePlant;

import java.util.ArrayList;

public class LockedPlantsLevel extends GamePlay {
    private ArrayList<BattlePlant> myPlants;

    @Override
    public void update() {}
    public ArrayList<BattlePlant> getMyPlants() {
        return myPlants;
    }
}
