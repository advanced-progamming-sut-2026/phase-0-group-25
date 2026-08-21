package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantCategory;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.*;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.Level;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.Ability;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities.ProducingSun;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlayGroundType.PlayGround;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.SunCollectedEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Model.Sun.RadioActiveSun;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;

import java.util.*;

public abstract class GamePlay {
    public static GamePlay activeInstance;
    protected float[] killedZombiesCostPerWave = new float[50];
    static int effectedTime = 3;
    static int spawnX = 1800;
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
    protected ArrayList<Wave> allWaves = new ArrayList<>();
    protected int level;
    protected ChapterType chapterType;
    protected Level levelObject;
    protected int ticksSinceLastDrop = 0;
    protected Random random = new Random();
    protected User thisUser;
    protected int timeToSpawn = 0;
    protected int numOfPlantFood;
    protected int mySuns;
    protected PlayGround playGround;
    public boolean isPaused;
    protected int numOfWaves;
    protected int totalTicksPassed = 0;
    protected int lostPlants = 0;
    protected boolean settedThePlants = false;
    private List<Integer> rowBag = new ArrayList<>();
    protected double totalTimePassed;
    protected ArrayList<DroppedPlantFood> activePlantFoods = new ArrayList<>();
    protected ArrayList<SandstormEffect> activeSandstorms = new ArrayList<>();
    protected ArrayList<IcyWindEffect> activeIcyWinds = new ArrayList<>();
    protected Set<String> boostedPlants = new HashSet<>();


    public GamePlay(ChapterType chapterType, int level, int difficulty, User thisUser,
                    ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        this.numOfPlantFood = thisUser.getUserProgress().getPlantFoodCount();
        this.mySuns = 1050;
        this.isPaused = false;
        this.level = level;
        this.chapterType = chapterType;
        this.thisUser = thisUser;
        activeInstance = this;

        if (boosted != null) {
            this.boostedPlants.addAll(boosted);
        }

        for (String pName : plants) {
            this.plants.add(PlantFactory.createBattlePlant(pName, getLevelOfPlant(pName)));
        }

        ArrayList<Zombie> tempZ = new ArrayList<>();
        for (String zName : zombies) {
            tempZ.add(ZombieFactory.createZombie(zName));
        }

        this.numOfWaves = calculateWaves(chapterType, level);
        for (int i = 1; i < numOfWaves; i++) {
            int waveCost = (int) (calculateCost(chapterType, level, i) * (1 + difficulty / 10.0));
            Wave thisWave = new Wave(waveCost, i);
            this.allWaves.add(thisWave);
            thisWave.zombieMaker(tempZ);
        }
        int waveCost = (int) ((calculateCost(chapterType, level, numOfWaves) + 500) * (1 + difficulty / 10.0));
        FinalWave thisFinal = new FinalWave(waveCost, numOfWaves);
        this.allWaves.add(thisFinal);
        thisFinal.zombieMaker(tempZ);


        if (chapterType == ChapterType.BIG_WAVE_BEACH) {
            for (int y = 1; y < 6; y++) {
                float startX = 430f;
                float startY = getRealY(y) + 15;
                mowers.add(new Mower(y, startX, startY));

                for (int x = 1; x < 10; x++) {
                    Position newPosition = new Position(x, y);
                    Boolean isArable = (x != 9 && x != 8);
                    Tile newTile = new Tile(newPosition, isArable, 0);
                    tiles.add(newTile);
                }
            }
        } else {
            for (int y = 1; y < 6; y++) {
                float startX = 430f;
                float startY = getRealY(y) + 15;
                mowers.add(new Mower(y, startX, startY));

                for (int x = 1; x < 10; x++) {
                    Position newPosition = new Position(x, y);
                    Boolean isArable = (Math.random() >= 0.06 || (x == 5 && (y == 2 || y == 4))) ||
                        (x == 1 || x == 2 || x == 3);
                    int tileHP = 0;
                    if (!isArable && (chapterType == ChapterType.ANCIENT_EGYPT || chapterType == ChapterType.DARK_AGE)) {
                        tileHP = 700;
                    } else if (chapterType == ChapterType.FROSTBITE_CAVES) {
                        tileHP = (Math.random() <= 0.5) ? 700 : 0;
                    }
                    Tile newTile = new Tile(newPosition, isArable, tileHP);

                    if (!isArable && chapterType == ChapterType.DARK_AGE) {
                        double rand = Math.random();
                        if (rand < 0.20) {
                            newTile.setGraveType(Tile.GraveType.PLANT_FOOD);
                        } else if (rand < 0.40) {
                            newTile.setGraveType(Tile.GraveType.SUN);
                        } else {
                            newTile.setGraveType(Tile.GraveType.NORMAL);
                        }

                        if (Math.random() <= 0.30) {
                            newTile.setNecromancy(true);
                        }
                    }

                    tiles.add(newTile);
                }
            }
        }
    }

    public static int calculateCost(ChapterType chapterType, int level, int waveNumber) {
        int chapterNumber = (chapterType != null) ? chapterType.ordinal() + 1 : 2;
        int levelNumber = (Set.of(1, 2, 3, 4).contains(level)) ? level : 1;
        int stagesPerChapter = 4;

        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + levelNumber;

        int baseDifficultyForStage = 800 + (globalLevel * 250);

        if (baseDifficultyForStage < 1000) {
            baseDifficultyForStage = 1000;
        }

        int waveMultiplier = (waveNumber - 1) * 550;

        int finalCost = baseDifficultyForStage + waveMultiplier;

        return finalCost;
    }

    public void incrementLostPlants() {
        this.lostPlants++;
    }

    public abstract void update();

    public Boolean checkingTheEndOfTheGame() {
        if (this.allWaves.isEmpty()) {
            return false;
        }

        for (Wave wave : this.allWaves) {
            if (!wave.getStarted() || wave.hasZombiesLeftToSpawn()) {
                return false;
            }
        }

        return this.gameZombies.isEmpty();
    }

    public void Pause() {
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
                            (int) posOfSun.getX(), (int) posOfSun.getY());

                    if (sun instanceof RadioActiveSun) {
                        Sun regularSun = new Sun(25, sun.getPosition(), 0);
                        activeSuns.set(i, regularSun);
                    }
                }
                sun.setTimeToReach(remainingTime);
            }
        }
    }

    public void checkingSunMakers() {
        for (BattlePlant p : this.gamePlants) {
            if (p.getCategory() == PlantCategory.SUN_PRODUCER) {
                for (Ability ability : p.getOriginalAbilities()) {
                    if (ability instanceof ProducingSun) {
                        if (!((ProducingSun) ability).isCollected() && ((ProducingSun) ability).isProduced()) {
                            addSun(((ProducingSun) ability).getSun());
                        }
                    }
                }
            }
        }
    }


    public void onWin() {
        if (levelObject != null) {
            levelObject.completeLevel();
        }

        int sunProducersCount = (int) gamePlants.stream()
                .filter(p -> p.getCategory() == PlantCategory.SUN_PRODUCER)
                .count();

        boolean[] emptyColumns = new boolean[10];
        boolean[] emptyRows = new boolean[6];
        Arrays.fill(emptyColumns, true);
        Arrays.fill(emptyRows, true);

        for (BattlePlant p : gamePlants) {
            emptyColumns[p.getColumn()] = false;
            emptyRows[p.getRow()] = false;
        }

        QuestManager.getInstance().notifyEvent(new LevelWonEvent(
                lostPlants, mySuns,
                thisUser.getUserProgress().getGameDifficulty(),
                sunProducersCount, emptyColumns, emptyRows
        ));
    }

    public void collectSun(int x, int y) {
        Sun targetSun = null;

        for (Sun sun : activeSuns) {
            if ((int) sun.getPosition().getX() == getRealX(x) && (int) sun.getPosition().getY() == getRealY(y)) {
                if (sun.isFromSky()) {
                    sun.setCollected(true);
                } else {
                    sun.setCollected(true);
                    Tile thisTile = getTileByPosition(x, y);
                    Ability thisAbility = thisTile.getPlants().get(0).getOriginalAbilities().get(0);
                    if (thisAbility instanceof ProducingSun) {
                        ((ProducingSun) thisAbility).setCollected(false);
                        ((ProducingSun) thisAbility).setProduced(false);
                    }
                }
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
            QuestManager.getInstance().notifyEvent(new SunCollectedEvent(targetSun.getNumberOfSun()));
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
        if (thisPlant.checkingPlantable(this.mySuns, thisTile) && thisTile.isArable()) {
            boolean isImitaterBoosted = false;
            int thisPX = (int) thisPosition.getX();
            int thisPY = (int) thisPosition.getY();
            String thisPName = thisPlant.getName();
            if (thisPName.equals("IMITATER")) {
                int number = new Random().nextInt(plants.size()) + 1;
                thisPName = this.plants.get(number).getName();
                isImitaterBoosted = getLevelOfPlant("IMITATER") == 4;
            }
            Position thisPPosition = new Position(getRealX(thisPX), getRealY(thisPY));
            BattlePlant thisP = PlantFactory.createBattlePlant(thisPName, getLevelOfPlant(thisPName), thisPPosition);
            thisP.setRow((int) thisPosition.getY());
            thisP.setColumn((int) thisPosition.getX());

            // Checking if this plant has a kind of boost...
            PlantType thisPlantType = PlantType.valueOf(thisPName);
            if (thisUser.getUserProgress().getGreenhouseBoosts().contains(thisPlantType) || isImitaterBoosted) {
                thisP.setEffected(true, effectedTime);
            }
            if (UsersManager.getInstance().hasGreenhouseBoost(thisPlantType)) {
                UsersManager.getInstance().consumeGreenhouseBoost(thisPlantType);
            }

            this.gamePlants.add(thisP);
            thisTile.addPlant(thisP);
            thisPlant.setCurrentCoolDown(thisPlant.getPlantStats().getRechargeTime());
            this.mySuns = Math.max(0, this.mySuns - thisPlant.getPlantStats().getCost());

            System.out.printf("%s was planted in (%d, %d)\n", thisPName, thisPX, thisPY);
        } else {
            if (!thisTile.isArable()) {
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
        Tile thisTile = getTileByPosition((int) thisPosition.getX(), (int) thisPosition.getY());
        System.out.println("The Plants :");
        for (BattlePlant p : thisTile.getPlants()) {
            System.out.println("->  " + p.getName() + " | HP : " + p.getCurrentHP());
            PlantStats ps = p.getPlantStats();
            System.out.printf("level: %d | cost: %d | baseHP: %d\n", ps.getLevel(),
                    ps.getCost(), ps.getBaseHP());
            System.out.println("Abilities :");
            for (String ability : ps.getAbilities()) {
                System.out.printf(" # %s", ability);
            }
            System.out.println();
        }
        System.out.println("The Zombies :");
        for (Zombie z : thisTile.getZombies()) {
            System.out.println("->  " + z.getName() + " | HP : " + z.getCurrentHP());
            System.out.println("Abilities");
            for (String ability : z.getZombieStats().getAbilities()) {
                System.out.printf(" # %s", ability);
            }
            System.out.println();
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
            boolean isPlantable = (this.mySuns >= plant.getPlantStats().getCost())
                    && (plant.getCurrentCoolDown() == 0 || !plant.getActiveCooldown());
            int cooldown = (int) plant.getCurrentCoolDown();

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

    public void setMySuns(int mySuns) {
        this.mySuns = mySuns;
    }

    public void cheatAddSun(int sun) {
        this.mySuns += sun;
        System.out.println("You added " + sun + " suns!!");
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

    public int calculateWaves(ChapterType chapterType, int level) {
        int chapterNumber = (chapterType != null) ? chapterType.ordinal() + 1 : 2;
        int levelNumber = (Set.of(1, 2, 3, 4).contains(level)) ? level : 1;

        int stagesPerChapter = 4;

        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + levelNumber;

        double calculatedWaves = 1.1 + (globalLevel * 0.26);

        int waves = (int) Math.floor(calculatedWaves);

        return Math.max(1, waves);
    }

    public void killAward(User thisUser) {
        boolean hasAward = Math.random() >= 0.9;
        int kindOfAward = (int) (Math.random() * 3) + 1;

        if (hasAward) {
            switch (kindOfAward) {
                case 1:
                    UsersManager.getInstance().addCoins(50);
                    int numOfCoins = thisUser.getUserProgress().getCoinsCount();
                    System.out.printf("A zombie dropped a coin; you have %d coins now.\n", numOfCoins);
                    UIManager.showToast("+50 Coins dropped!", "IMAGE_UI_GENERIC_VTB");
                    break;
                case 2:
                    UsersManager.getInstance().addGems(1);
                    int numOfGems = thisUser.getUserProgress().getGemsCount();
                    System.out.printf("A zombie dropped a diamond; you have %d diamonds now.\n", numOfGems);
                    UIManager.showToast("+1 Diamond dropped!", "IMAGE_UI_GENERIC_VTB");
                    break;
                case 3:
                    UsersManager.getInstance().addPots(1);
                    int numOfPots = thisUser.getUserProgress().getPotsCount();
                    System.out.printf("A zombie dropped a pot; you have %d pots now.\n", numOfPots);
                    UIManager.showToast("+1 Pot unlocked!", "IMAGE_UI_GENERIC_VTB");
                    break;
            }
        }
    }

    public void glowingAward(Position zombiePosition) {
        Position dropPos = new Position(zombiePosition.getX(), zombiePosition.getY());
        activePlantFoods.add(new DroppedPlantFood(dropPos));
        System.out.println("A glowing zombie dropped Plant Food!");
        UIManager.showToast("Plant Food dropped!", "IMAGE_UI_GENERIC_VTB");
    }

    public boolean tryCollectPlantFoodByHover(float mouseX, float mouseY) {
        DroppedPlantFood target = null;
        for (DroppedPlantFood pf : activePlantFoods) {
            float px = (float) pf.getPosition().getX() + 30;
            float py = (float) pf.getPosition().getY() + 30;

            if (Math.hypot(mouseX - px, mouseY - py) <= 65) {
                target = pf;
                break;
            }
        }

        if (target != null) {
            if (this.numOfPlantFood < 3) {
                addPlantFood();
                activePlantFoods.remove(target);
                return true;
            }
        }
        return false;
    }

    public void updateZombieTiles() {
        for (Tile tile : tiles) {
            tile.getZombies().clear();
            // Removing broken graves for the game
            if (chapterType == ChapterType.ANCIENT_EGYPT || chapterType == ChapterType.DARK_AGE) {
                if (tile.getHP() <= 0 && !tile.isArable() && !tile.isHole()) {
                    tile.setArable(true);
                }
            }
        }

        for (Zombie zombie : gameZombies) {
            Position zombiePosition = Position.getRowAndColumn(zombie.getPosition());
            int currentX = (int) zombiePosition.getX();
            int currentY = (int) zombiePosition.getY();
            Tile currentTile = getTileByPosition(currentX, currentY);

            zombie.setRow(currentY);
            zombie.setColumn(currentX);

            if (zombie.getPosition().getX() <= 20) {
                zombie.setCurrentHP(0);
                zombie.setAlive(false);
                continue;
            }

            if (currentTile == null) {
                continue;
            }

            if (!currentTile.isArable() && chapterType == ChapterType.FROSTBITE_CAVES && currentTile.getHP() == 0) {
                zombie.changeRow();
                System.out.println("Changing the row...!!!");
                zombiePosition = Position.getRowAndColumn(zombie.getPosition());
                currentX = (int) zombiePosition.getX();
                currentY = (int) zombiePosition.getY();
                currentTile = getTileByPosition(currentX, currentY);
            }

            if (currentTile != null) {
                currentTile.getZombies().add(zombie);
            }
        }
    }

    public String getKindOfTile(ChapterType thisChapter) {
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
        return tiles.stream()
                .filter(t -> (int) t.getPosition().getX() == x &&
                        (int) t.getPosition().getY() == y)
                .findFirst()
                .orElse(null);
    }

    public int getRandomTime() {
        int[] numbers = {200, 350, 300, 450};
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

    public int getLevelOfPlant(String plantName) {
        PlantType thisPlantType = PlantType.valueOf(plantName);
        return thisUser.getUserProgress().getUnlockedPlantsAndTheirLevels().get(thisPlantType);
    }

    public int getRealX(int gridX) {
        return (int) Math.round(566.1 + ((gridX - 1) * 152.2));
    }

    public int getRealY(int gridY) {
        return 205 + ((gridY - 1) * 150);
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

    public ArrayList<BattlePlant> getGamePlants() {
        return gamePlants;
    }

    public ArrayList<Sun> getActiveSuns() {
        return activeSuns;
    }

    public int getTotalTicksPassed() {
        return totalTicksPassed;
    }

    public double getTotalTimePassed() {
        return totalTimePassed;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public ArrayList<Dynamite> getDynamites() {
        return dynamites;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void applyIcyWind() {
        if (chapterType == ChapterType.FROSTBITE_CAVES && Math.random() < 0.012) {
            int randomNumber = new Random().nextInt(5) + 1;
            System.out.println("An icy wind blew in row " + randomNumber + "!");

            activeIcyWinds.add(new IcyWindEffect(randomNumber));

            for (BattlePlant bp : this.gamePlants) {
                Position thisPos = Position.getRowAndColumn(bp.getPosition());
                if (thisPos.getY() == randomNumber) {
                    bp.setIceTime(bp.getIceTime() + 1);
                }
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }

    public boolean tryCollectSunByClick(float clickX, float clickY) {
        Sun targetSun = null;

        for (Sun sun : activeSuns) {
            float sx = (float) sun.getPosition().getX() + 40;
            float sy = (float) sun.getPosition().getY() + 40;

            if (Math.hypot(clickX - sx, clickY - sy) <= 60) {
                targetSun = sun;
                break;
            }
        }

        if (targetSun == null) return false;

        if (targetSun.isFromSky()) {
            targetSun.setCollected(true);
        } else {
            targetSun.setCollected(true);
            Position gridPos = Position.getRowAndColumn(targetSun.getPosition());
            Tile thisTile = getTileByPosition((int) gridPos.getX(), (int) gridPos.getY());
            if (thisTile != null && !thisTile.getPlants().isEmpty()) {
                Ability thisAbility = thisTile.getPlants().get(0).getOriginalAbilities().get(0);
                for (BattlePlant bp : thisTile.getPlants()) {
                    if (bp.getPlantStats().getCategory().equals("Sun Producer")) {
                        bp.setLastActionTime(this.getTotalTimePassed());
                    }
                }
                if (thisAbility instanceof ProducingSun) {
                    ((ProducingSun) thisAbility).setCollected(false);
                    ((ProducingSun) thisAbility).setProduced(false);
                }
            }
        }

        if (targetSun instanceof RadioActiveSun && targetSun.getTimeToReach() > 0) {
            handleRadioActiveExploration((RadioActiveSun) targetSun);
        } else {
            addSun(targetSun);
            QuestManager.getInstance().notifyEvent(new SunCollectedEvent(targetSun.getNumberOfSun()));
        }

        activeSuns.remove(targetSun);
        return true;
    }

    public ArrayList<Mower> getMowers() {
        return mowers;
    }

    public void setTotalTimePassed(double totalTimePassed) {
        this.totalTimePassed = totalTimePassed;
    }

    public float getProgressPercentage() {
        if (allWaves.isEmpty()) return 1.0f;

        int total = allWaves.size();
        int started = 0;
        for (Wave w : allWaves) {
            if (w.getStarted()) started++;
        }

        if (started == 0) return 0f;


        float baseProgress = (float) (started - 1) / total;


        double difficultyMultiplier = 1 + (thisUser.getUserProgress().getGameDifficulty() / 10.0);
        float currentWaveTotalCost;
        if (started == total) {
            currentWaveTotalCost = (float) ((calculateCost(chapterType, level, total) + 500) * difficultyMultiplier);
        } else {
            currentWaveTotalCost = (float) (calculateCost(chapterType, level, started) * difficultyMultiplier);
        }


        float killedCost = killedZombiesCostPerWave[started];


        float depletion = killedCost / currentWaveTotalCost;

        float fraction;
        if (started < total) {

            fraction = depletion / 0.75f;
        } else {
            fraction = depletion;
        }

        if (fraction > 1.0f) fraction = 1.0f;

        return Math.min(baseProgress + (fraction * (1.0f / total)), 1.0f);
    }

    public void addKilledZombieCost(int waveNum, float cost) {
        if (waveNum >= 0 && waveNum < killedZombiesCostPerWave.length) {
            killedZombiesCostPerWave[waveNum] += cost;
        }
    }

    public ArrayList<DroppedPlantFood> getActivePlantFoods() {
        return activePlantFoods;
    }

    public User getThisUser() {
        return thisUser;
    }

    public ArrayList<SandstormEffect> getActiveSandstorms() {
        return activeSandstorms;
    }

    public void addSandstormEffect(float x, float y) {
        activeSandstorms.add(new SandstormEffect(x, y));
    }

    public ArrayList<IcyWindEffect> getActiveIcyWinds() {
        return activeIcyWinds;
    }

    public boolean isPlantBoosted(String plantName) {
        if (boostedPlants.contains(plantName)) return true;
        PlantType type = PlantType.fromName(plantName);
        return type != null && thisUser != null && thisUser.getUserProgress() != null
            && thisUser.getUserProgress().hasGreenhouseBoost(type);
    }

    public boolean usePlantFood(int gridX, int gridY) {
        if (this.numOfPlantFood <= 0) return false;
        Tile tile = getTileByPosition(gridX, gridY);
        if (tile != null && !tile.getPlants().isEmpty()) {
            applyPlantFood(gridX, gridY);
            this.numOfPlantFood--;
            System.out.printf("Plant food applied on plant at (%d, %d). Remaining: %d\n", gridX, gridY, this.numOfPlantFood);
            return true;
        }
        return false;
    }
}
