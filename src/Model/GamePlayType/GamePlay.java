package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Model.ChaptersAndLevels.Level;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Dynamite;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Sun.RadioActiveSun;
import src.Model.Tile;
import src.Model.PlayGroundType.PlayGround;
import src.Model.Sun.Sun;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.Model.Wave.FinalWave;
import src.Model.Wave.Wave;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public abstract class GamePlay {
    // plants that can appear in the game...
    protected ArrayList<BattlePlant> plants = new ArrayList<>();

    // plants and zombies that are in the game at the moment...
    protected ArrayList<Zombie> gameZombies = new ArrayList<>();
    protected ArrayList<BattlePlant> gamePlants = new ArrayList<>();

    protected ArrayList<Tile> tiles = new ArrayList<>();
    protected ArrayList<Projectile> projectiles = new ArrayList<>();
    protected ArrayList<Dynamite> dynamites = new ArrayList<>();
    protected ArrayList<Mower> mowers = new ArrayList<>();
    protected ArrayList<Sun> activeSuns = new ArrayList<>();
    protected ArrayList<Grave> allGraves = new ArrayList<>();
    protected ArrayList<Wave> allWaves = new ArrayList<>();

    protected int level;
    protected ChapterType chapterType;
    protected Level levelObject;

    protected int ticksSinceLastDrop = 0;
    protected Random random = new Random();
    private List<Integer> rowBag = new ArrayList<>();
    protected  User thisUser;
    protected int timeToSpwan = 0;
    static int effectedTime = 3;
    static int spawnX = 1800;
    protected int numOfPlantFood;
    protected int mySuns;
    protected PlayGround playGround;
    protected boolean isPaused;
    protected int numOfWaves;
    protected int totalTicksPassed = 0;

    public GamePlay(ChapterType chapterType, int level, int difficulty, User thisUser,
                    ArrayList<String> plants, ArrayList<String> zombies, Set<String > boosted) {
        this.numOfPlantFood = 0;
        this.mySuns = 0;
        this.isPaused = false;
        this.level = level;
        this.chapterType = chapterType;
        this.thisUser = thisUser;

        for (String pName : plants) {
            Position PPos = new Position(1, 1);
            this.plants.add(PlantFactory.createBattlePlant(pName, getLevelOfPlant(pName), PPos));
        }

        ArrayList <Zombie> tempZ = new ArrayList<>();
        for (String zName : zombies) {
            tempZ.add(ZombieFactory.createZombie(zName, new Position(1 , 1)));
        }

        this.numOfWaves = calculateWaves(chapterType, level);
        for (int i = 1 ; i < numOfWaves ; i++) {
            int waveCost = (int)(calculateCost(chapterType, level, i) * (1 + difficulty/10.0));
            Wave thisWave = new Wave(waveCost, i);
            this.allWaves.add(thisWave);
            thisWave.zombieMaker(tempZ);
        }
        int waveCost = (int)((calculateCost(chapterType, level, numOfWaves) + 500) * (1 + difficulty/10.0));
        FinalWave thisFinal = new FinalWave(waveCost, numOfWaves);
        this.allWaves.add(thisFinal);
        thisFinal.zombieMaker(tempZ);

        this.playGround = new PlayGround() {
            @Override
            public void makeGround() {
                if (chapterType == ChapterType.BIG_WAVE_BEACH) {
                    for (int y = 1; y < 6; y++) {
                        mowers.add (new Mower(y));
                        for (int x = 1 ; x < 10; x++) {
                            Position newPosition = new Position(x, y);
                            Boolean isArable = (x != 9 && x != 8);
                            Tile newTile = new Tile(newPosition, isArable);
                            tiles.add(newTile);
                        }
                    }
                } else {
                    for (int y = 1; y < 6; y++) {
                        mowers.add(new Mower(y));
                        for (int x = 1; x < 10; x++) {
                            Position newPosition = new Position(x, y);
                            Boolean isArable = Math.random() >= 0.06 || (x == 5 && (y == 2 || y== 4));
                            Tile newTile = new Tile(newPosition, isArable);
                            tiles.add(newTile);
                        }
                    }
                }
            }
        };
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
            Position spawnPosition = new Position(getRealX(spawnX), getRealY(spawnY));

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
                    Position posOfSun = Position.getRowAndColumn(sun.getPosition());
                    System.out.printf("Sun reached the ground at position (%d, %d)\n",
                            (int)posOfSun.getX(), (int)posOfSun.getY());

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
            if ((int)sun.getPosition().getX() == getRealX(x) && (int)sun.getPosition().getY() == getRealY(y)) {
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

        // Damage for zombies :
        for (Zombie zombie : gameZombies) {
            int zX = (int) zombie.getPosition().getX();
            int zY = (int) zombie.getPosition().getY();

            if (Math.abs(sunX - zX) <= 500 && Math.abs(sunY - zY) <= 500) {
                zombie.takeDamage(150);
            }
        }

        // Damage for plants :
        for (BattlePlant plant : gamePlants) {
            int pX = (int) plant.getPosition().getX();
            int pY = (int) plant.getPosition().getY();

            if (Math.abs(sunX - pX) <= 200 && Math.abs(sunY - pY) <= 200) {
                plant.setCurrentHP(plant.getCurrentHP() - 80);
            }
        }
    }

    public void planting(BattlePlant thisPlant, Position thisPosition) {
        Tile thisTile = getTileByPosition((int) thisPosition.getX(), (int) thisPosition.getY());

        if (thisPlant.checkingPlantable(mySuns, thisTile) && thisTile.isArable()) {
            int thisPX = (int) thisPosition.getY();
            int thisPY = (int) thisPosition.getY();
            String thisPName = thisPlant.getName();
            Position thisPPosition = new Position(getRealX(thisPX), getRealY(thisPY));
            BattlePlant thisP = PlantFactory.createBattlePlant(thisPName, getLevelOfPlant(thisPName), thisPPosition);
            thisP.setRow((int) thisPosition.getY());
            thisP.setColumn((int) thisPosition.getX());

            this.gamePlants.add(thisP);
            thisTile.addPlant(thisP);
            this.mySuns -= thisPlant.getPlantStats().getCost();

            System.out.printf("%s was planted in (%d, %d)", thisPName, thisPX, thisPY);
        } else {
            if(!thisTile.isArable()) {
                System.out.println("This tile is not arable! Try another one...!");
            } else {
                System.out.println("You can't plant this plant!");
            }
        }
    }

    public void plucking(Position thisPosition) {
        Tile thisTile = getTileByPosition((int) thisPosition.getX(), (int) thisPosition.getY());

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

    public void showMap() {
        System.out.println("=== GAME STATUS ===");
        System.out.println("Current Wave: " + getCurrentWave() + " / " + this.numOfWaves);
        System.out.println("Sun Amount: " + this.mySuns);
        System.out.println("Plant Foods: " + this.numOfPlantFood);
        System.out.println("===================");

        System.out.println("Board Legend:");
        System.out.printf("[ ] : Arable Tile     ~ ~ : %s Tile\n", getKindOfTile(chapterType));
        System.out.println(" P  : Plant is present       Z  : Zombie is present");
        System.out.println(" M  : Mower (Ready)          X  : Mower (Used)");
        System.out.println("---------------------------------------------------------");
        for (int y = 1; y <= 5; y++) {
            final int currentY = y;

            Mower currentMower = mowers.stream()
                    .filter(m -> m.getY() == currentY)
                    .findFirst()
                    .orElse(null);

            if (currentMower != null && !currentMower.isUsed()) {
                System.out.print("(M) ");
            } else {
                System.out.print("(X) ");
            }

            for (int x = 1; x <= 9; x++) {
                final int currentX = x;

                Tile currentTile = getTileByPosition(currentX, currentY);

                if (currentTile != null) {
                    boolean hasPlant = !currentTile.getPlants().isEmpty();
                    boolean hasZombie = !currentTile.getZombies().isEmpty();

                    char plantChar = hasPlant ? 'P' : ' ';
                    char zombieChar = hasZombie ? 'Z' : ' ';

                    if (currentTile.isArable()) {
                        System.out.printf("[%c %c] ", plantChar, zombieChar);
                    } else {
                        System.out.printf("~%c %c~ ", plantChar, zombieChar);
                    }
                }
            }
            System.out.println();
        }
        System.out.println("---------------------------------------------------------");
    }

    public void showPlantsStatus() {
        System.out.println("=== Plants Status ===");

        for (BattlePlant plant : this.plants) {
            String name = plant.getName();
            int cost = plant.getPlantStats().getCost();
            boolean isPlantable = plant.checkingSunCooldown(this.mySuns);
            int cooldown = plant.getCooldown();

            System.out.printf("- %s:\n", name);
            System.out.printf("  Sun required: %d\n", cost);

            if (isPlantable) {
                System.out.println("  Status: Ready to plant!");
            } else {
                System.out.print("  Status: Not ready.");

                if (this.mySuns < cost) {
                    System.out.printf(" (Not enough sun! You need %d more)", cost - this.mySuns);
                }

                if (cooldown > 0) {
                    System.out.printf(" (Cooldown: %d seconds left to be plantable)", cooldown);
                }

                System.out.println();
            }
        }
        System.out.println("=====================");
    }

    public int getNumOfPlantFood() {
        return numOfPlantFood;
    }

    public int getCurrentWave() {
        int currentWave = 1;
        for (Wave wave : this.allWaves) {
            if (wave.getStarted()) {
                currentWave = wave.getWaveNum();
            }
        }
        return currentWave;
    }

    public ArrayList<Zombie> getGameZombies() {
        return gameZombies;
    }

    public void applyPlantFood(int x, int y) {
        Tile thisTile = getTileByPosition(x, y);
        for (BattlePlant p : thisTile.getPlants()) {
            p.setEffected(true, effectedTime);
        }
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

    public void killAward (User thisUser) {
        Boolean hasAward = Math.random() >= 0.9;
        int kindOfAward = (int)(Math.random() * 3) + 1;

        if (hasAward) {
            switch (kindOfAward) {
                case 1:
                    UsersManager.getInstance().addCoins(50);
                    int numOfCoins = thisUser.getUserProgress().getCoinsCount();
                    System.out.printf("A zombie dropped a coin; you have %d coins now.\n", numOfCoins);
                    break;
                case 2:
                    UsersManager.getInstance().addGems(1);
                    int numOfGems = thisUser.getUserProgress().getGemsCount();
                    System.out.printf("A zombie dropped a diamond; you have %d diamonds now.\n", numOfGems);
                    break;
                case 3:
                    UsersManager.getInstance().addPots(1);
                    int numOfPots = thisUser.getUserProgress().getPotsCount();
                    System.out.printf("A zombie dropped a pot; you have %d pots now.\n", numOfPots);
                    break;
            }
        }
    }

    public void glowingAward (GamePlay thisGame) {
        Boolean isGlowing = Math.random() <= 0.05;
        if (isGlowing) {
            thisGame.addPlantFood();
            System.out.printf("The glowing zombie dropped a plant food; you have %d plant foods now.\n",
                                    thisGame.getNumOfPlantFood());
        }
    }

    public void updateZombieTiles() {
        for (Tile tile : tiles) {
            tile.getZombies().clear();
        }

        for (Zombie zombie : gameZombies) {
            Position zombiePosition = Position.getRowAndColumn(zombie.getPosition());
            int currentX = (int) zombiePosition.getX();
            int currentY = (int) zombiePosition.getY();

            Tile currentTile = getTileByPosition(currentX, currentY);

            if (currentTile != null) {
                currentTile.getZombies().add(zombie);
            }
        }
    }

    public String getKindOfTile (ChapterType thisChapter) {
        if (thisChapter == ChapterType.ANCIENT_EGYPT ||
            thisChapter == ChapterType.DARK_AGE) {
            return "Grave";
        } else if (thisChapter == ChapterType.FROSTBITE_CAVES) {
            return "Landslide";
        } else {
            return "Water";
        }
    }

    public Tile getTileByPosition(int x, int y) {
        Tile thisTIle = tiles.stream()
                .filter(t -> (int) t.getPosition().getX() == x &&
                        (int) t.getPosition().getY() == y)
                        .findFirst()
                        .orElse(null);
        return thisTIle;
    }

    public int getRandomTime() {
        int[] numbers = {10, 40, 30, 50};
        int randomIndex = (int) (Math.random() * numbers.length);
        return numbers[randomIndex];
    }

    public int getNextRandomY() {
        if (rowBag.isEmpty()) {
            for (int i = 1; i <= 5; i++) {
                rowBag.add(i);
            }
            Collections.shuffle(rowBag, this.random);
        }

        return rowBag.remove(0);
    }

    public int getLevelOfPlant (String plantName) {
        PlantType thisPlantType = PlantType.valueOf(plantName);
        return thisUser.getUserProgress().getUnlockedPlantsAndTheirLevels().get(thisPlantType);
    }

    public int getRealX(int gridX) {
        return 120 + ((gridX - 1) * 200);
    }

    public int getRealY(int gridY) {
        return 140 + ((gridY - 1) * 200);
    }

    public Level getLevelObject() {
        return levelObject;
    }

    public void setLevelObject(Level levelObject) {
        this.levelObject = levelObject;
    }

    public int getLevel() {
        return level;
    }

    public ChapterType getChapterType() {
        return chapterType;
    }

    public ArrayList<BattlePlant> getPlants() {
        return plants;
    }
}