package src.Model.GamePlayType;

import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Position;
import src.Model.Tile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Sun.Sun;
import java.util.ArrayList;

public abstract class GamePlay {
    protected ArrayList<Zombie> gameZombies;
    protected ArrayList<Plant> gamePlants;
    protected ArrayList<Tile> tiles;

    protected int numOfPlantFood;
    protected int mySuns;

    protected PlayGround playGround;
    protected boolean isPaused;
    protected int waveCount;

    public GamePlay() {
        this.numOfPlantFood = 0;
        this.mySuns = 0;
        this.playGround = new PlayGround() {
            @Override
            public void makeGround() {
                for (int x = 1 ; x < 10; x++) {
                    for (int y = 1; y < 6; y++) {
                        Position newPosition = new Position(x, y);
                        Tile newTile = new Tile(newPosition);
                        tiles.add(newTile);
                    }
                }
            }
        };
        this.isPaused = false;
        this.waveCount = 1;
    }

    public void addWave(){
    }

    public abstract void update();

    public void finishGame() {

    }

    public void Pause(){
        this.isPaused = true;
    }

    public void advanceTime(int ticks) {
        for (int i = 0; i < ticks; i++) {
            update();
        }
    }

    public void sunMaker() {
    }

    public void planting(Plant thisPlant, Position thisPosition) {
        Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();
        thisTile.setPlant(thisPlant);
    }

    public void plucking(Plant thisPlant, Position thisPosition) {
        Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();
        thisTile.setPlant(null);
    }

    public void spawnZombies(){
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

    public void calculateSumOfZombiesHealth () {

    }


    //cheat functions...
    public void cheatAddSun() {
    }

    public void removeCooldown() {
    }

    public void cheatAddPlantFood() {
    }
}