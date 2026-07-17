package src.Model.GamePlayType;

import java.util.ArrayList;

public class LockedPlantsLevel extends GamePlay {
    private ArrayList<src.Model.PlantsAndZombies.BattlePlant> myPlants;

    @Override
    public void update() {}
    public ArrayList<src.Model.PlantsAndZombies.BattlePlant> getMyPlants() {
        return myPlants;
    }
}
