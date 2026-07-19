package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Sun.RadioActiveSun;
import src.Model.Tile;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Sun.Sun;
import src.Model.Wave.FinalWave;
import src.Model.Wave.Wave;

import java.util.ArrayList;

public abstract class GamePlay {
    // plants that can appear in the game...
    protected ArrayList<BattlePlant> plants = new ArrayList<>();

    // plants and zombies that are in the game at the moment...
    protected ArrayList<Zombie> gameZombies = new ArrayList<>();
    protected ArrayList<BattlePlant> gamePlants = new ArrayList<>();

    protected ArrayList<Wave> allWaves = new ArrayList<>();

    protected ArrayList<Tile> tiles = new ArrayList<>();
    protected ArrayList<Projectile> projectiles = new ArrayList<>();
    protected ArrayList<Mower> mowers = new ArrayList<>();
    protected ArrayList<Sun> activeSuns = new ArrayList<>();
    protected int ticksSinceLastDrop = 0;
    protected java.util.Random random = new java.util.Random();

    // TODO : checking isAlive and delete zombies in update...

    protected int numOfPlantFood;
    protected int mySuns;
    protected PlayGround playGround;
    protected boolean isPaused;
    protected int numOfWaves;
    protected int totalTicksPassed = 0;

    public GamePlay(ChapterType chapterType, int level) {
        this.numOfPlantFood = 0;
        this.mySuns = 0;
        this.isPaused = false;

        this.numOfWaves = calculateWaves(chapterType, level);
        for (int i = 1 ; i < numOfWaves ; i++) {
            int waveCost = calculateCost(chapterType, level, i);
            this.allWaves.add(new Wave(waveCost, i));
        }
        int waveCost = calculateCost(chapterType, level, numOfWaves) + 500;
        this.allWaves.add(new FinalWave(waveCost, numOfWaves));

        this.playGround = new PlayGround() {
            @Override
            public void makeGround() {
                for (int y = 1; y < 6; y++) {
                    mowers.add (new Mower(y));
                    for (int x = 1 ; x < 10; x++) {
                        Position newPosition = new Position(x, y);
                        Boolean isArable = Math.random() >= 0.06;
                        Tile newTile = new Tile(newPosition, isArable);
                        tiles.add(newTile);
                    }
                }
            }
        };

        //TODO: adding the zombies...
        //TODO: adding the plants...
    }

    public abstract void update() ;

    public Boolean checkingTheEndOfTheGame() {
        if (this.gameZombies.isEmpty()) {
            return true;
        } else {
            return false;
        }
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
                zombie.takeDamage(150);
                if (!zombie.isAlive()) {
                    System.out.printf("Zombie of type %s is dead at (%d, %d)\n", zombie.getName(), zX, zY);
                }
            }
        }

        for (BattlePlant plant : gamePlants) {
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

    public void planting(BattlePlant thisPlant, Position thisPosition) {
        Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();

        if (thisPlant.checkingPlantable(mySuns) && thisTile.isArable()) {
            this.gamePlants.add(thisPlant);

            thisTile.addPlant(thisPlant);

            thisPlant.setCooldown(40);

            this.mySuns -= thisPlant.getPlantStats().getCost();

            thisPlant.setRow((int) thisPosition.getY());
            thisPlant.setColumn((int) thisPosition.getX());

            System.out.printf("%s was planted in (%d, %d)", thisPlant.getName(),
                                    (int) thisPosition.getX(), (int) thisPosition.getY());
        } else {
            if(!thisTile.isArable()) {
                System.out.println("This tile is not arable! Try another one...!");
            } else {
                System.out.println("You can't plant this plant!");
            }
        }
    }

    public void plucking(BattlePlant thisPlant, Position thisPosition) {
        Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();
        if (thisTile.getPlants().isEmpty()) {
            System.out.println("There is no plants in this tile!!");
        } else {
            this.gamePlants.removeIf(p -> p.getRow() == (int) thisPosition.getY()
                                                        && p.getColumn() == (int) thisPosition.getX());
            thisTile.removePlant();
            System.out.println("Plunked successfully!");
        }
    }

    public void addPlantFood() {
        this.numOfPlantFood = Math.min(this.numOfPlantFood + 1, 3);
    }

    public void addSun(Sun sun) {
        this.mySuns += sun.getNumberOfSun();
    }

    public void showSunAmount() {
        System.out.printf("You have %d suns\n", this.mySuns);
    }

    public void showTileStatus(Position thisPosition) {
        Tile thisTile = tiles.stream().filter(p -> p.getPosition().equals(thisPosition)).findFirst().get();
        System.out.println("The Plants :");
        for (BattlePlant p : thisTile.getPlants()) {
            System.out.println("->  " + p.getName() + " | HP : " + p.getCurrentHP());
            PlantStats ps = p.getPlantStats();
            System.out.printf("level: %d | cost: %d | baseHP: %d\n", ps.getLevel(),
                                                                ps.getCost(), ps.getBaseHP());
            System.out.println("Abilities");
            for (String ability : ps.getAbilities()) {
                System.out.printf(" # %s", ability);
            }
        }
        System.out.println("The Zombies :");
        for (Zombie z : thisTile.getZombies()) {
            System.out.println("->  " + z.getName() + " | HP : " + z.getCurrentHP());
            System.out.println("Abilities");
            for (String ability : z.getAbilities()) {
                System.out.printf(" # %s", ability);
            }
        }
    }

    public void feedPlant() {
    }

    public void calculateSumOfZombiesHealth () {

    }

    public ArrayList<Zombie> getGameZombies() {
        return gameZombies;
    }

    public ArrayList<BattlePlant> getGamePlants() {
        return gamePlants;
    }

    public int getMySuns() {
        return mySuns;
    }

    public void cheatAddSun(int sun) {
        this.mySuns += sun;
        System.out.println("You added" + sun + "suns!!");
    }

    public void releaseTheNuke() {
        for (Zombie z : this.gameZombies) {
            z.setAlive(false);
        }
        System.out.println("You killed all zombies in the map!!");
    }

    public void removeCooldown() {
        for (BattlePlant p : this.plants) {
            p.inactivateCooldown();
        }
    }

    public static int calculateCost(ChapterType chapterType, int level, int waveNumber) {
        int chapterNumber = chapterType.ordinal() + 1;
        int stagesPerChapter = 4;

        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + level;

        int baseDifficultyForStage = 800 + (globalLevel * 250);

        if (baseDifficultyForStage < 1000) {
            baseDifficultyForStage = 1000;
        }

        int waveMultiplier = (waveNumber - 1) * 550;

        int finalCost = baseDifficultyForStage + waveMultiplier;

        return finalCost;
    }

    public int calculateWaves(ChapterType chapterType, int level) {
        int chapterNumber = chapterType.ordinal() + 1;

        int stagesPerChapter = 4;

        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + level;

        double calculatedWaves = 1.1 + (globalLevel * 0.26);

        int waves = (int) Math.floor(calculatedWaves);

        return Math.max(1, waves);
    }
}