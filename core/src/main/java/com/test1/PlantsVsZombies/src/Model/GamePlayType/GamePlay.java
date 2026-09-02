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
import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelStartedEvent;
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
    public static int effectedTime = 3;
    public static int spawnX = 1800;

    protected ArrayList<BattlePlant> plants = new ArrayList<>();
    protected ArrayList<Zombie> gameZombies = new ArrayList<>();
    protected ArrayList<BattlePlant> gamePlants = new ArrayList<>();
    protected ArrayList<Tile> tiles = new ArrayList<>();
    protected ArrayList<Projectile> projectiles = new ArrayList<>();
    protected ArrayList<Dynamite> dynamites = new ArrayList<>();
    protected ArrayList<Mower> mowers = new ArrayList<>();
    protected ArrayList<Sun> activeSuns = new ArrayList<>();
    protected ArrayList<Wave> allWaves = new ArrayList<>();

    protected ArrayList<BattlePlant> pendingNewPlants = new ArrayList<>();
    protected boolean updatingPlants = false;
    protected ArrayList<Zombie> pendingNewZombies = new ArrayList<>();
    protected boolean updatingZombies = false;

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
    private final List<Integer> rowBag = new ArrayList<>();
    protected double totalTimePassed;
    protected ArrayList<DroppedPlantFood> activePlantFoods = new ArrayList<>();
    protected ArrayList<SandstormEffect> activeSandstorms = new ArrayList<>();
    protected ArrayList<IcyWindEffect> activeIcyWinds = new ArrayList<>();
    protected Set<String> boostedPlants = new HashSet<>();

    private String levelObjectives;
    private boolean gameOver = false;
    private boolean won = false;

    public GamePlay(ChapterType chapterType, int level, int difficulty, User thisUser,
                    ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        this.numOfPlantFood = thisUser.getUserProgress().getPlantFoodCount();
        this.mySuns = 1050;
        this.isPaused = false;
        this.level = level;
        this.chapterType = chapterType;
        this.thisUser = thisUser;
        activeInstance = this;

        QuestManager.getInstance().notifyEvent(new LevelStartedEvent(chapterType, level, difficulty));

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

        BoardInitializer.initializeBoard(chapterType, mowers, tiles, this);
    }

    public static int calculateCost(ChapterType chapterType, int level, int waveNumber) {
        int chapterNumber = (chapterType != null) ? chapterType.ordinal() + 1 : 2;
        int levelNumber = (Set.of(1, 2, 3, 4).contains(level)) ? level : 1;
        int stagesPerChapter = 4;
        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + levelNumber;
        int baseDifficultyForStage = Math.max(1000, 800 + (globalLevel * 250));
        int waveMultiplier = (waveNumber - 1) * 550;
        return baseDifficultyForStage + waveMultiplier;
    }
    public int calculateWaves(ChapterType chapterType, int level) {
        int chapterNumber = (chapterType != null) ? chapterType.ordinal() + 1 : 2;
        int levelNumber = (Set.of(1, 2, 3, 4).contains(level)) ? level : 1;
        int stagesPerChapter = 4;
        int globalLevel = ((chapterNumber - 1) * stagesPerChapter) + levelNumber;
        return Math.max(1, (int) Math.floor(1.1 + (globalLevel * 0.26)));
    }
    public abstract void update();
    public Boolean checkingTheEndOfTheGame() {
        if (this.allWaves.isEmpty()) return false;
        for (Wave wave : this.allWaves) {
            if (!wave.getStarted() || wave.hasZombiesLeftToSpawn()) return false;
        }
        return this.gameZombies.isEmpty();
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

            if (chance < 80) {
                newSun = new Sun(25, spawnPosition, 5.0);
            } else if (chance < 95) {
                newSun = new Sun(100, spawnPosition, 5.0);
            } else {
                newSun = new RadioActiveSun(25, spawnPosition);
            }

            activeSuns.add(newSun);
            ticksSinceLastDrop = 0;
        }

        for (int i = 0; i < activeSuns.size(); i++) {
            Sun sun = activeSuns.get(i);
            if (sun.getTimeToReach() > 0) {
                double remainingTime = sun.getTimeToReach() - 0.1;
                if (remainingTime <= 0.001) {
                    remainingTime = 0;
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
                    if (ability instanceof ProducingSun ps && !ps.isCollected() && ps.isProduced()) {
                        addSun(ps.getSun());
                    }
                }
            }
        }
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
                for (BattlePlant bp : thisTile.getPlants()) {
                    if ("Sun Producer".equals(bp.getPlantStats().getCategory())) {
                        bp.setLastActionTime(this.getTotalTimePassed());
                    }
                }
                Ability thisAbility = thisTile.getPlants().get(0).getOriginalAbilities().get(0);
                if (thisAbility instanceof ProducingSun ps) {
                    ps.setCollected(false);
                    ps.setProduced(false);
                }
                thisTile.getPlants().get(0).setLastActionTime(this.getTotalTimePassed());
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
    private void handleRadioActiveExploration(RadioActiveSun radSun) {
        int sunX = (int) radSun.getPosition().getX();
        int sunY = (int) radSun.getPosition().getY();
        for (Zombie zombie : gameZombies) {
            if (Math.abs(sunX - (int) zombie.getPosition().getX()) <= 500 && Math.abs(sunY - (int) zombie.getPosition().getY()) <= 500) {
                zombie.takeDamage(150);
            }
        }
        for (BattlePlant plant : gamePlants) {
            if (Math.abs(sunX - (int) plant.getPosition().getX()) <= 200 && Math.abs(sunY - (int) plant.getPosition().getY()) <= 200) {
                plant.setCurrentHP(plant.getCurrentHP() - 80);
            }
        }
    }
    private boolean hasFrozen(Tile thisTile) {
        if (thisTile == null) return false;
        for (BattlePlant p : thisTile.getPlants()) {
            if (p.isFrozen()) return true;
        }
        return false;
    }
    public void planting(BattlePlant thisPlant, Position thisPosition) {
        Tile thisTile = getTileByPosition((int) thisPosition.getX(), (int) thisPosition.getY());
        if (thisTile == null) return;
        boolean hasWater = thisPlant.getPlantStats().getTags().contains("Water") || thisPlant.getPlantStats().getTags().contains("water");
        boolean canPlant = (chapterType == ChapterType.BIG_WAVE_BEACH) ? hasWater ^ thisTile.isArable() : !hasWater && thisTile.isArable();
        boolean isHotPotato = "HOT_POTATO".equals(thisPlant.getName());
        boolean checkingPlantable = (chapterType == ChapterType.FROSTBITE_CAVES && isHotPotato) ?
            hasFrozen(thisTile) : !isHotPotato && thisPlant.checkingPlantable(this.mySuns, thisTile);

        if (checkingPlantable && canPlant) {
            boolean isImitaterBoosted = false;
            int thisPX = (int) thisPosition.getX();
            int thisPY = (int) thisPosition.getY();
            String thisPName = thisPlant.getName();

            if (thisPName.equals("IMITATER")) {
                int number = new Random().nextInt(plants.size());
                thisPName = this.plants.get(number).getName();
                isImitaterBoosted = getLevelOfPlant("IMITATER") == 4;
            }

            Position thisPPosition = new Position(getRealX(thisPX), getRealY(thisPY));
            BattlePlant thisP = PlantFactory.createBattlePlant(thisPName, getLevelOfPlant(thisPName), thisPPosition);
            thisP.setRow((int) thisPosition.getY());
            thisP.setColumn((int) thisPosition.getX());

            boolean isBoosted = isPlantBoosted(thisPName) || isImitaterBoosted;
            if (isBoosted) {
                thisP.setEffected(true, effectedTime);
                thisP.setLastActionTime(this.getTotalTimePassed());
            }

            PlantType thisPlantType = PlantType.fromName(thisPName);
            if (thisPlantType != null && UsersManager.getInstance().hasGreenhouseBoost(thisPlantType)) {
                UsersManager.getInstance().consumeGreenhouseBoost(thisPlantType);
            }

            this.gamePlants.add(thisP);
            thisTile.addPlant(thisP);
            thisPlant.setCurrentCoolDown(thisPlant.getPlantStats().getRechargeTime());
            this.mySuns = Math.max(0, this.mySuns - thisPlant.getPlantStats().getCost());
        }
    }
    public BattlePlant plantFromPlantFood(BattlePlant sourcePlant, Position position) {
        Tile tile = getTileByPosition(
            (int) position.getX(),
            (int) position.getY()
        );

        if (tile == null) return null;

        if (sourcePlant.getName().equals(PlantType.LILY_PAD.getName())) {
            Position realPosition = new Position(
                getRealX((int) position.getX()),
                getRealY((int) position.getY())
            );

            BattlePlant newPlant = PlantFactory.createBattlePlant(
                sourcePlant.getName(),
                sourcePlant.getPlantStats().getLevel(),
                realPosition
            );

            newPlant.setRow((int) position.getY());
            newPlant.setColumn((int) position.getX());

            if (updatingPlants) {
                pendingNewPlants.add(newPlant);
            } else {
                gamePlants.add(newPlant);
            }

            tile.addPlant(newPlant);

            return newPlant;
        }

        if (!tile.isArable() || !tile.getPlants().isEmpty()) {
            return null;
        }

        Position realPosition = new Position(
            getRealX((int) position.getX()),
            getRealY((int) position.getY())
        );

        BattlePlant newPlant = PlantFactory.createBattlePlant(
            sourcePlant.getName(),
            sourcePlant.getPlantStats().getLevel(),
            realPosition
        );

        newPlant.setRow((int) position.getY());
        newPlant.setColumn((int) position.getX());

        if (updatingPlants) {
            pendingNewPlants.add(newPlant);
        } else {
            gamePlants.add(newPlant);
        }

        tile.addPlant(newPlant);

        return newPlant;
    }
    public Zombie addZombieFromAbility(Zombie zombie) {
        if (zombie == null) return null;

        if (updatingZombies) {
            pendingNewZombies.add(zombie);
        } else {
            gameZombies.add(zombie);
        }

        return zombie;
    }
    public void plucking(Position thisPosition) {
        Tile thisTile = getTileByPosition((int) thisPosition.getX(), (int) thisPosition.getY());
        if (thisTile != null && !thisTile.getPlants().isEmpty()) {
            this.gamePlants.removeIf(p -> p.getRow() == (int) thisPosition.getY() && p.getColumn() == (int) thisPosition.getX());
            thisTile.removePlant();
        }
    }
    public void onWin() {
        if (levelObject != null) levelObject.completeLevel();

        int sunProducersCount = (int) gamePlants.stream()
            .filter(p -> p.getPlantStats().getCategory().equals(PlantCategory.SUN_PRODUCER.getString()))
            .count();

        boolean[] emptyColumns = new boolean[9];
        boolean[] emptyRows = new boolean[5];
        Arrays.fill(emptyColumns, true);
        Arrays.fill(emptyRows, true);

        for (BattlePlant p : gamePlants) {
            emptyColumns[p.getColumn() - 1] = false;
            emptyRows[p.getRow() - 1] = false;
        }

        QuestManager.getInstance().notifyEvent(new LevelWonEvent(
            lostPlants, mySuns, thisUser.getUserProgress().getGameDifficulty(),
            sunProducersCount, emptyColumns, emptyRows
        ));
    }
    public void killAward(User thisUser) {
        if (Math.random() >= 0.9) {
            int kindOfAward = (int) (Math.random() * 3) + 1;
            switch (kindOfAward) {
                case 1 -> {
                    UsersManager.getInstance().addCoins(50);
                    UIManager.showToast("+50 Coins dropped!", "IMAGE_UI_GENERIC_VTB");
                }
                case 2 -> {
                    UsersManager.getInstance().addGems(1);
                    UIManager.showToast("+1 Diamond dropped!", "IMAGE_UI_GENERIC_VTB");
                }
                case 3 -> {
                    UsersManager.getInstance().addPots(1);
                    UIManager.showToast("+1 Pot unlocked!", "IMAGE_UI_GENERIC_VTB");
                }
            }
        }
    }
    public void glowingAward(Position zombiePosition) {
        activePlantFoods.add(new DroppedPlantFood(new Position(zombiePosition.getX(), zombiePosition.getY())));
        UIManager.showToast("Plant Food dropped!", "IMAGE_UI_GENERIC_VTB");
    }
    public boolean tryCollectPlantFoodByHover(float mouseX, float mouseY) {
        DroppedPlantFood target = null;
        for (DroppedPlantFood pf : activePlantFoods) {
            if (Math.hypot(mouseX - (float) pf.getPosition().getX() - 30, mouseY - (float) pf.getPosition().getY() - 30) <= 65) {
                target = pf;
                break;
            }
        }
        if (target != null && this.numOfPlantFood < 3) {
            addPlantFood();
            activePlantFoods.remove(target);
            return true;
        }
        return false;
    }
    public void updateZombieTiles() {
        for (Tile tile : tiles) {
            tile.getZombies().clear();
            if (chapterType == ChapterType.ANCIENT_EGYPT || chapterType == ChapterType.DARK_AGE) {
                if (tile.getHP() <= 0 && !tile.isArable() && !tile.isHole()) {
                    tile.setArable(true);
                }
            } else if (chapterType == ChapterType.BIG_WAVE_BEACH) {
                if ((int) tile.getPosition().getX() == 8 || (int) tile.getPosition().getX() == 9) {
                    boolean hasLilyPad = tile.getPlants().stream().anyMatch(p -> "LILY_PAD".equals(p.getName()));
                    tile.setArable(hasLilyPad);
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

            if (currentTile != null && !currentTile.isArable() && chapterType == ChapterType.FROSTBITE_CAVES && currentTile.getHP() == 0
                && !zombie.getName().equals("DODO")) {
                zombie.changeRow();
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
    public void updatePlantTiles() {
        for (Tile tile : tiles) {
            if (tile != null && tile.getPlants() != null) {
                tile.getPlants().clear();
            }
        }
        for (BattlePlant plant : gamePlants) {
            if (plant != null && plant.isAlive()) {
                Tile targetTile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (targetTile != null) {
                    targetTile.addPlant(plant);
                }
            }
        }
    }
    public void triggerLowTide() {
        if (chapterType != ChapterType.BIG_WAVE_BEACH) return;
        boolean spawnedAny = false;
        for (Tile tile : tiles) {
            if (!tile.isArable() && tile.isLowTide() && !tile.isLowTideTriggered()) {
                int col = (int) tile.getPosition().getX();
                int row = (int) tile.getPosition().getY();
                Position spawnPos = new Position(getRealX(col), getRealY(row));
                Zombie zombie = ZombieFactory.createZombie("DEFAULT", spawnPos);
                zombie.setRow(row);
                zombie.setColumn(col);
                gameZombies.add(zombie);
                tile.setLowTideTriggered(true);
                spawnedAny = true;
            }
        }
        if (spawnedAny) {
            UIManager.showToast("Low Tide! Zombies rising from the water!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
        }
    }
    public void applyIcyWind() {
        if (chapterType == ChapterType.FROSTBITE_CAVES && Math.random() < 0.012) {
            int randomNumber = new Random().nextInt(5) + 1;
            activeIcyWinds.add(new IcyWindEffect(randomNumber));
            for (BattlePlant bp : this.gamePlants) {
                if (Position.getRowAndColumn(bp.getPosition()).getY() == randomNumber) {
                    bp.setIceTime(bp.getIceTime() + 1);
                }
            }
        }
    }
    public float getProgressPercentage() {
        if (allWaves.isEmpty()) return 1.0f;
        int total = allWaves.size();
        int started = (int) allWaves.stream().filter(Wave::getStarted).count();
        if (started == 0) return 0f;

        float baseProgress = (float) (started - 1) / total;
        double difficultyMultiplier = 1 + (thisUser.getUserProgress().getGameDifficulty() / 10.0);
        float currentWaveTotalCost = (float) ((started == total ? calculateCost(chapterType, level, total) + 500 : calculateCost(chapterType, level, started)) * difficultyMultiplier);
        float killedCost = killedZombiesCostPerWave[started];
        float depletion = killedCost / currentWaveTotalCost;
        float fraction = Math.min(1.0f, (started < total) ? depletion / 0.75f : depletion);

        return Math.min(baseProgress + (fraction * (1.0f / total)), 1.0f);
    }
    public boolean usePlantFood(int gridX, int gridY) {
        if (this.numOfPlantFood <= 0) return false;
        Tile tile = getTileByPosition(gridX, gridY);
        if (tile != null && !tile.getPlants().isEmpty()) {
            applyPlantFood(gridX, gridY);
            this.numOfPlantFood--;
            UsersManager.getInstance().reducePlantFood(1);
            return true;
        }
        return false;
    }
    public void applyPlantFood(int x, int y) {
        Tile thisTile = getTileByPosition(x, y);
        if (thisTile != null) {
            for (BattlePlant p : thisTile.getPlants()) p.setEffected(true, effectedTime);
        }
    }
    public boolean isPlantBoosted(String plantName) {
        if (boostedPlants.contains(plantName)) return true;
        PlantType type = PlantType.fromName(plantName);
        return type != null && thisUser != null && thisUser.getUserProgress() != null && thisUser.getUserProgress().hasGreenhouseBoost(type);
    }
    public void addPlantFood() {
        this.numOfPlantFood = Math.min(this.numOfPlantFood + 1, 3);
        UsersManager.getInstance().addPlantFood(1);
    }
    public void addSun(Sun sun) {
        this.mySuns += sun.getNumberOfSun();
    }
    public void cheatAddSun(int sun) {
        this.mySuns += sun;
    }
    public void incrementLostPlants() {
        this.lostPlants++;
    }
    public void addKilledZombieCost(int waveNum, float cost) {
        if (waveNum >= 0 && waveNum < killedZombiesCostPerWave.length) killedZombiesCostPerWave[waveNum] += cost;
    }
    protected void endGame(boolean won) {
        this.won = won;
        this.gameOver = true;
        this.isPaused = true;
    }
    public int getNextRandomY() {
        if (rowBag.isEmpty()) {
            for (int i = 1; i <= 5; i++) rowBag.add(i);
            Collections.shuffle(rowBag, this.random);
        }
        return rowBag.remove(0);
    }
    public int getRandomTime() {
        int[] numbers = {200, 350, 300, 450};
        return numbers[(int) (Math.random() * numbers.length)];
    }
    public int getLevelOfPlant(String plantName) {
        PlantType t = PlantType.valueOf(plantName);
        Integer lvl = thisUser.getUserProgress().getUnlockedPlantsAndTheirLevels().get(t);
        return lvl != null ? lvl : 1;
    }
    public int getRealX(int gridX) {
        return (int) Math.round(566.1 + ((gridX - 1) * 152.2));
    }
    public int getRealY(int gridY) {
        return 205 + ((gridY - 1) * 150);
    }
    public Tile getTileByPosition(int x, int y) {
        return tiles.stream().filter(t -> (int) t.getPosition().getX() == x && (int) t.getPosition().getY() == y).findFirst().orElse(null);
    }
    public int getNumOfPlantFood() {
        return numOfPlantFood;
    }
    public ArrayList<Zombie> getGameZombies() {
        return gameZombies;
    }
    public void setMySuns(int mySuns) {
        this.mySuns = mySuns;
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
    public void setTotalTimePassed(double totalTimePassed) {
        this.totalTimePassed = totalTimePassed;
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
    public ArrayList<Mower> getMowers() {
        return mowers;
    }
    public ArrayList<DroppedPlantFood> getActivePlantFoods() {
        return activePlantFoods;
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
    public User getThisUser() {
        return thisUser;
    }
    public boolean isPaused() {
        return isPaused;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public boolean hasWon() {
        return won;
    }
    public String getLevelObjectives() {
        return levelObjectives;
    }
    public void setLevelObjectives(String levelObjectives) {
        this.levelObjectives = levelObjectives;
    }

    public int getMySuns() {
        return mySuns;
    }
}
