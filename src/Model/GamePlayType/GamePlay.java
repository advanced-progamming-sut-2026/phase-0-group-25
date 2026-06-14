package src.Model.GamePlayType;

import src.Model.PlayGroundType.PlayGround;

public abstract class GamePlay {
    private int numOfPlantFood;
    private int mySuns;
    private PlayGround playGround;
    private boolean isPaused;
    private int waveCount;
    public void addWave(){

    }

    public abstract void update();

    public void finishGame() {
    }
    public void Pause(){

    }

    public void advanceTime() {
    }

    public void sunMaker() {
    }

    public void planting() {
    }

    public void spawnZombies(){

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
