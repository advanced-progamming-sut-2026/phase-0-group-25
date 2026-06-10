package Model.GamePlayType;

import Model.PlayGroundType.PlayGround;

public abstract class GamePlay {
    private int numOfPlantFood;
    private int mySuns;
    private PlayGround playGround;

    public abstract void update();

    public void finishGame() {
    }

    public void advanceTime() {
    }

    public void sunMaker() {
    }

    public void planting() {
    }

    public void plucking() {
    }

    public void addPlantFood() {
    }

    public void addSun() {
    }

    public void showMap() {
    }

    public void feedPlant() {
    }



    //cheat functions...
    public void cheatAddSun() {
    }

    public void removeCooldown() {
    }

    public void cheatAddPlantFood() {
    }
}
