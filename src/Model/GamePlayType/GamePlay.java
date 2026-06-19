package src.Model.GamePlayType;

import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Sun.Sun;

import java.util.ArrayList;

public abstract class GamePlay {
    protected ArrayList<Zombie> gameZombies;
    protected ArrayList<Plant> gamePlants;
    protected int numOfPlantFood;
    protected int mySuns;
    protected PlayGround playGround;
    protected boolean isPaused;
    protected int waveCount;

    public void addWave(){
    }

    public abstract void update();

    public void finishGame() {
    }

    public void Pause(){
    }

    public void advanceTime(int ticks) {
        for (int i = 0; i < ticks; i++) {
            update();
        }
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
        this.numOfPlantFood += 1;
    }

    public void addSun(Sun sun) {
        this.mySuns += sun.getNumberOfSun();
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