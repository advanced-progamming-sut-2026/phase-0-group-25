package Model.GamePlayType;

import java.util.ArrayList;

public class LoveYourPlants extends GamePlay {
    private ArrayList<Model.PlantsAndZombies.Plant> myPlants;
    private int numOfLost = 0;

    @Override
    public void update() {}
    private void addNumOfLost() {}
    public ArrayList<Model.PlantsAndZombies.Plant> getMyPlants() {
        return myPlants;
    }
}
