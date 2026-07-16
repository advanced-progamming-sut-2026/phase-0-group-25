package src.Model.GamePlayType;

import src.Model.Mower;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Position;
//import src.Model.PlantsAndZombies.Projectile;
import src.Model.Sun.RadioActiveSun;
import src.Model.Tile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Sun.Sun;
import java.util.ArrayList;

public abstract class GamePlay {
    // plants and zombies that can appear in the game...
    protected ArrayList<Zombie> zombies;
    protected ArrayList<Plant> plants;

    // plants and zombies that are in the game at the moment...
    protected ArrayList<Zombie> gameZombies;
    protected ArrayList<Plant> gamePlants;

    protected ArrayList<Tile> tiles;
//    protected ArrayList<Projectile> projectiles;
    protected ArrayList<Mower> mowers;
    protected ArrayList<Sun> activeSuns = new ArrayList<>();
    protected int ticksSinceLastDrop = 0;
    protected java.util.Random random = new java.util.Random();

    // TODO : checking isAlive and delete zombies in update...

    protected int numOfPlantFood;
    protected int mySuns;
    protected PlayGround playGround;
    protected boolean isPaused;
    protected int waveCount;
    protected int totalTicksPassed = 0;

    public GamePlay() {
        this.numOfPlantFood = 0;
        this.mySuns = 0;
        this.playGround = new PlayGround() {
            @Override
            public void makeGround() {
                for (int y = 1; y < 6; y++) {
                    mowers.add (new Mower(y));
                    for (int x = 1 ; x < 10; x++) {
                        Position newPosition = new Position(x, y);
                        Tile newTile = new Tile(newPosition);
                        tiles.add(newTile);
                    }
                }
            }
        };
        this.isPaused = false;
        this.waveCount = 1;
        //TODO: adding the zombies...
        //TODO: adding the plants...
    }

    public void addWave(){
    }

    public abstract void update() ;

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
        ticksSinceLastDrop++;

        double t = totalTicksPassed / 10.0;

        double x = Math.max(6 + 0.05 * t, 12);
        int requiredTicksForNextDrop = (int) (x * 10);

        if (ticksSinceLastDrop >= requiredTicksForNextDrop) {
            int spawnX = random.nextInt(9) + 1;
            int spawnY = random.nextInt(5) + 1;
            Position spawnPosition = new Position(spawnX, spawnY);

            int chance = random.nextInt(100);
            Sun newSun;
            String sunTypeString;

            if (chance < 80) {
                newSun = new Sun(25, spawnPosition, 5.0);
                sunTypeString = "regular";
            } else if (chance < 95) {
                newSun = new Sun(100, spawnPosition, 5.0);
                sunTypeString = "special";
            } else {
                newSun = new RadioActiveSun(25, spawnPosition);
                sunTypeString = "radioactive";
            }

            activeSuns.add(newSun);
            ticksSinceLastDrop = 0;

            System.out.printf("New %s sun is dropping at position (%d, %d)\n", sunTypeString, spawnX, spawnY);
        }

        for (int i = 0; i < activeSuns.size(); i++) {
            Sun sun = activeSuns.get(i);

            if (sun.getTimeToReach() > 0) {
                double remainingTime = sun.getTimeToReach() - 0.1;

                if (remainingTime <= 0.001) {
                    remainingTime = 0;
                    System.out.printf("Sun reached the ground at position (%d, %d)\n",
                            (int)sun.getPosition().getX(), (int)sun.getPosition().getY());

                    if (sun instanceof RadioActiveSun) {
                        Sun regularSun = new Sun(25, sun.getPosition(), 0);
                        activeSuns.set(i, regularSun);
                    }
                }
                sun.setTimeToReach(remainingTime);
            }
        }
    }

    public void collectSun(int x, int y) {
        Sun targetSun = null;

        for (Sun sun : activeSuns) {
            if ((int)sun.getPosition().getX() == x && (int)sun.getPosition().getY() == y) {
                targetSun = sun;
                break;
            }
        }

        if (targetSun == null) {
            System.out.println("No sun found at this position!");
            return;
        }

        if (targetSun instanceof RadioActiveSun && targetSun.getTimeToReach() > 0) {
            handleRadioActiveExploration((RadioActiveSun) targetSun);
            activeSuns.remove(targetSun);
        } else {
            addSun(targetSun);
            activeSuns.remove(targetSun);
        }
    }

    private void handleRadioActiveExploration(RadioActiveSun radSun) {
        int sunX = (int) radSun.getPosition().getX();
        int sunY = (int) radSun.getPosition().getY();

        for (Zombie zombie : gameZombies) {
            int zX = (int) zombie.getPosition().getX();
            int zY = (int) zombie.getPosition().getY();

            if (Math.abs(sunX - zX) <= 2 && Math.abs(sunY - zY) <= 2) {
                zombie.setCurrentHP(zombie.getCurrentHP() - 150);
                if (zombie.getCurrentHP() <= 0) {
                    zombie.setAlive(false);
//                    System.out.printf("Zombie of type %s is dead at (%d, %d)\n", zombie.getName(), zX, zY);
                }
            }
        }

        for (Plant plant : gamePlants) {
            int pX = (int) plant.getPosition().getX();
            int pY = (int) plant.getPosition().getY();

            if (Math.abs(sunX - pX) <= 1 && Math.abs(sunY - pY) <= 1) {
                plant.setCurrentHP(plant.getCurrentHP() - 80);
                if (plant.getCurrentHP() <= 0) {
                    plant.setAlive(false);
                    plucking(plant, plant.getPosition());
                    System.out.printf("Plant %s at (%d, %d) is destroyed.\n", plant.getName(), pX, pY);
                }
            }
        }
    }

    public void planting(Plant thisPlant, Position thisPosition) {
        if (thisPlant.checkingPlantable(mySuns)) {
            Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();
            thisTile.setPlant(thisPlant);
            System.out.printf("%s was planted in (%d, %d)", thisPlant.getName(),
                                    (int) thisPosition.getX(), (int) thisPosition.getY());
        } else {
            System.out.println("You can't plant this plant!");
        }
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

    public ArrayList<Zombie> getGameZombies() {
        return gameZombies;
    }

    public ArrayList<Plant> getGamePlants() {
        return gamePlants;
    }

    //cheat functions...
    public void cheatAddSun() {
    }

    public void removeCooldown() {
    }

    public void cheatAddPlantFood() {
    }
}