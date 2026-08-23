package com.test1.PlantsVsZombies.src.Model.User;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.MiniGameType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.ZombieType;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenhousePlant;

import java.time.LocalDate;
import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProgress {
    private HashMap<ChapterType, Integer> unlockedChaptersAndLevels;
    private ArrayList<ZombieType> unlockedZombies;
    private HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels;
    private int gemsCount;
    private int coinsCount;
    private int gameDifficulty;
    private int gamesPlayed;
    private Map<String, String> questVariables;

    // New settings
    private int gameSpeed = 1;          // 1, 2, or 3
    private boolean showTileGrid = false;

    private Map<MiniGameType, Integer> miniGameLevels;

    private Map<String, Integer> questProgress;
    private List<String> completedQuestIds;
    private List<String> claimedQuestIds;
    private LocalDate lastDailyReset;

    private int plantFoodCount;
    private Map<PlantType, Integer> seedPackets;
    private LocalDate dailyOfferPurchaseDate;

    // Persisted so the daily offer stays the same plant across app restarts,
    // and only actually changes when the calendar day changes.
    private PlantType dailyOfferPlantType;
    private int dailyOfferPrice;
    private int dailyOfferSeedPacketCount;
    private LocalDate dailyOfferGeneratedDate;

    private int miniGamesCompleted;
    private int dailyQuestsCompleted;
    private int nonDailyQuestsCompleted;

    private boolean[][] unlockedPots;
    private GreenhousePlant[][] potPlants;
    private Set<PlantType> greenhouseBoosts;

    private static int potRowCount = 3;
    private static int potColumnCount = 4;

    public UserProgress() {
        this.unlockedChaptersAndLevels = new HashMap<>();
        this.unlockedZombies = new ArrayList<>();
        this.unlockedPlantsAndTheirLevels = new HashMap<>();

        this.gamesPlayed = 0;
        this.gemsCount = 0;
        this.coinsCount = 0;
        this.gameDifficulty = 3;
        this.gameSpeed = 1;
        this.showTileGrid = false;

        this.plantFoodCount = 0;
        this.seedPackets = new HashMap<>();
        this.dailyOfferPurchaseDate = null;

        this.unlockedPots = new boolean[potRowCount][potColumnCount];
        this.potPlants = new GreenhousePlant[potRowCount][potColumnCount];
        this.greenhouseBoosts = new HashSet<>();

        for (int x = 0; x < potColumnCount; x++) {
            unlockedPots[0][x] = true;
        }

        this.miniGamesCompleted = 0;
        this.dailyQuestsCompleted = 0;
        this.nonDailyQuestsCompleted = 0;
        this.questVariables = new HashMap<>();

        this.questProgress = new HashMap<>();
        this.completedQuestIds = new ArrayList<>();
        this.claimedQuestIds = new ArrayList<>();
        this.lastDailyReset = null;

        this.miniGameLevels = new HashMap<>();
        for (MiniGameType type : MiniGameType.values()) {
            miniGameLevels.put(type, 1);
        }
    }

    public static int getPotRowCount() {
        return potRowCount;
    }

    public static int getPotColumnCount() {
        return potColumnCount;
    }

    // ----- Existing getters/setters (unchanged) -----
    public Map<MiniGameType, Integer> getMiniGameLevels() {
        return miniGameLevels;
    }
    public void setMiniGameLevels(Map<MiniGameType, Integer> miniGameLevels) {
        this.miniGameLevels = miniGameLevels;
    }
    public int getMiniGameLevel(MiniGameType type) {
        return miniGameLevels.getOrDefault(type, 1);
    }
    public int getMiniGamesCompleted() {
        return miniGamesCompleted;
    }
    public int getDailyQuestsCompleted() {
        return dailyQuestsCompleted;
    }
    public int getNonDailyQuestsCompleted() {
        return nonDailyQuestsCompleted;
    }
    public Map<String, String> getQuestVariables() {
        return questVariables;
    }
    void setQuestVariables(Map<String, String> questVariables) {
        this.questVariables = questVariables;
    }
    void incrementMiniGamesCompleted() {
        this.miniGamesCompleted++;
    }
    void incrementDailyQuestsCompleted() {
        this.dailyQuestsCompleted++;
    }
    void incrementNonDailyQuestsCompleted() {
        this.nonDailyQuestsCompleted++;
    }
    public Map<String, Integer> getQuestProgress() {
        return questProgress;
    }
    void setQuestProgress(Map<String, Integer> questProgress) {
        this.questProgress = questProgress;
    }
    public List<String> getCompletedQuestIds() {
        return completedQuestIds;
    }
    void setCompletedQuestIds(List<String> completedQuestIds) {
        this.completedQuestIds = completedQuestIds;
    }
    public List<String> getClaimedQuestIds() {
        return claimedQuestIds;
    }
    void setClaimedQuestIds(List<String> claimedQuestIds) {
        this.claimedQuestIds = claimedQuestIds;
    }
    public LocalDate getLastDailyReset() {
        return lastDailyReset;
    }
    void setLastDailyReset(LocalDate lastDailyReset) {
        this.lastDailyReset = lastDailyReset;
    }
    public boolean[][] getUnlockedPots() {
        return unlockedPots;
    }
    void setUnlockedPots(boolean[][] unlockedPots) {
        this.unlockedPots = unlockedPots;
    }
    public GreenhousePlant[][] getPotPlants() {
        return potPlants;
    }
    void setPotPlants(GreenhousePlant[][] potPlants) {
        this.potPlants = potPlants;
    }
    public Set<PlantType> getGreenhouseBoosts() {
        return greenhouseBoosts;
    }
    void setGreenhouseBoosts(Set<PlantType> greenhouseBoosts) {
        this.greenhouseBoosts = greenhouseBoosts;
    }
    public int getPotsCount() {
        int count = 0;
        for (int y = 0; y < potRowCount; y++) {
            for (int x = 0; x < potColumnCount; x++) {
                if (unlockedPots[y][x]) count++;
            }
        }
        return count;
    }
    void unlockPot(int x, int y) {
        if (x < 1 || x > potColumnCount || y < 1 || y > potRowCount) return;
        unlockedPots[y - 1][x - 1] = true;
    }
    void unlockNextPot() {
        for (int y = 0; y < potRowCount; y++) {
            for (int x = 0; x < potColumnCount; x++) {
                if (!unlockedPots[y][x]) {
                    unlockedPots[y][x] = true;
                    return;
                }
            }
        }
    }
    void plantInPot(int x, int y, GreenhousePlant plant) {
        if (x < 1 || x > potColumnCount || y < 1 || y > potRowCount) return;
        potPlants[y - 1][x - 1] = plant;
    }
    void removePlantFromPot(int x, int y) {
        if (x < 1 || x > potColumnCount || y < 1 || y > potRowCount) return;
        potPlants[y - 1][x - 1] = null;
    }
    void addGreenhouseBoost(PlantType plant) {
        greenhouseBoosts.add(plant);
    }
    public boolean hasGreenhouseBoost(PlantType plant) {
        return greenhouseBoosts.contains(plant);
    }
    void consumeGreenhouseBoost(PlantType plant) {
        greenhouseBoosts.remove(plant);
    }
    public int getPlantFoodCount() {
        return plantFoodCount;
    }
    void setPlantFoodCount(int count) {
        this.plantFoodCount = Math.min(count, 3);
    }
    public Map<PlantType, Integer> getSeedPackets() {
        return seedPackets;
    }
    void setSeedPackets(Map<PlantType, Integer> seedPackets) {
        this.seedPackets = seedPackets;
    }
    public void addSeedPackets(PlantType plant, int amount) {
        seedPackets.put(plant, seedPackets.getOrDefault(plant, 0) + amount);
    }
    public LocalDate getDailyOfferPurchaseDate() {
        return dailyOfferPurchaseDate;
    }
    void setDailyOfferPurchaseDate(LocalDate date) {
        this.dailyOfferPurchaseDate = date;
    }
    public boolean isDailyOfferBoughtToday() {
        return dailyOfferPurchaseDate != null && dailyOfferPurchaseDate.equals(LocalDate.now());
    }
    public PlantType getDailyOfferPlantType() {
        return dailyOfferPlantType;
    }
    void setDailyOfferPlantType(PlantType plantType) {
        this.dailyOfferPlantType = plantType;
    }
    public int getDailyOfferPrice() {
        return dailyOfferPrice;
    }
    void setDailyOfferPrice(int price) {
        this.dailyOfferPrice = price;
    }
    public int getDailyOfferSeedPacketCount() {
        return dailyOfferSeedPacketCount;
    }
    void setDailyOfferSeedPacketCount(int seedPacketCount) {
        this.dailyOfferSeedPacketCount = seedPacketCount;
    }
    public LocalDate getDailyOfferGeneratedDate() {
        return dailyOfferGeneratedDate;
    }
    void setDailyOfferGeneratedDate(LocalDate date) {
        this.dailyOfferGeneratedDate = date;
    }
    /**
     * True only if a daily offer was generated today AND the plant it was
     * generated for is still unlocked. Used to decide whether a saved
     * offer can be reused as-is or must be regenerated.
     */
    public boolean hasValidPersistedDailyOffer() {
        return dailyOfferGeneratedDate != null
            && dailyOfferGeneratedDate.equals(LocalDate.now())
            && dailyOfferPlantType != null
            && unlockedPlantsAndTheirLevels.containsKey(dailyOfferPlantType);
    }
    public HashMap<ChapterType, Integer> getUnlockedChaptersAndLevels() {
        return unlockedChaptersAndLevels;
    }
    void setUnlockedChaptersAndLevels(HashMap<ChapterType, Integer> unlockedChaptersAndLevels) {
        this.unlockedChaptersAndLevels = unlockedChaptersAndLevels;
    }
    public int getGamesPlayed() {
        return gamesPlayed;
    }
    void addGamesPlayed() {
        this.gamesPlayed++;
    }
    public ArrayList<ZombieType> getUnlockedZombies() {
        return unlockedZombies;
    }
    void setUnlockedZombies(ArrayList<ZombieType> unlockedZombies) {
        this.unlockedZombies = unlockedZombies;
    }
    public HashMap<PlantType, Integer> getUnlockedPlantsAndTheirLevels() {
        return unlockedPlantsAndTheirLevels;
    }
    void setUnlockedPlantsAndTheirLevels(HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels) {
        this.unlockedPlantsAndTheirLevels = unlockedPlantsAndTheirLevels;
    }
    public int extractTotalLevelsPassed() {
        int total = 0;
        for (Integer level : unlockedChaptersAndLevels.values()) {
            total += level;
        }
        return total;
    }
    public int getGemsCount() {
        return gemsCount;
    }
    void setGemsCount(int gemsCount) {
        this.gemsCount = gemsCount;
    }
    public int getCoinsCount() {
        return coinsCount;
    }
    void setCoinsCount(int coinsCount) {
        this.coinsCount = coinsCount;
    }
    void addCoins(int amount) {
        if (amount > 0) this.coinsCount += amount;
    }
    void addGems(int amount) {
        if (amount > 0) this.gemsCount += amount;
    }
    public int getGameDifficulty() {
        return gameDifficulty;
    }
    void setGameDifficulty(int gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    // ----- NEW getters/setters for settings -----
    public int getGameSpeed() {
        return gameSpeed;
    }
    void setGameSpeed(int gameSpeed) {
        if (gameSpeed < 1) gameSpeed = 1;
        if (gameSpeed > 3) gameSpeed = 3;
        this.gameSpeed = gameSpeed;
    }
    public boolean isShowTileGrid() {
        return showTileGrid;
    }
    void setShowTileGrid(boolean showTileGrid) {
        this.showTileGrid = showTileGrid;
    }

    // ----- Existing methods (unchanged) -----
    public int getPlantLevel(PlantType plantType) {
        return unlockedPlantsAndTheirLevels.getOrDefault(plantType, 0);
    }
    void unlockPlant(PlantType plantType) {
        unlockedPlantsAndTheirLevels.put(plantType, 1);
    }
    void unlockZombie(ZombieType zombieType) {
        unlockedZombies.add(zombieType);
    }
    void unlockChapter(ChapterType chapterType) {
        unlockedChaptersAndLevels.put(chapterType, 0);
    }
    void markLevelCompleted(ChapterType chapterType, int level) {
        unlockedChaptersAndLevels.put(chapterType, level);
    }
    public void subtractCoins(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot subtract negative amount.");
        if (this.coinsCount < amount) throw new IllegalArgumentException("Insufficient coins.");
        this.coinsCount -= amount;
    }
    public void subtractGems(int amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot subtract negative amount.");
        if (this.gemsCount < amount) throw new IllegalArgumentException("Insufficient gems.");
        this.gemsCount -= amount;
    }
    public boolean hasEnoughSeedPackets(PlantType plant, int required) {
        return seedPackets.getOrDefault(plant, 0) >= required;
    }

    void deductSeedPackets(PlantType plant, int amount) {
        if (amount < 0) throw new IllegalArgumentException("Cannot deduct negative amount.");
        int current = seedPackets.getOrDefault(plant, 0);
        if (current < amount) throw new IllegalArgumentException("Not enough seed packets.");
        seedPackets.put(plant, current - amount);
    }

    void upgradePlant(PlantType plant) {
        if (!unlockedPlantsAndTheirLevels.containsKey(plant))
            throw new IllegalArgumentException("Plant not unlocked.");
        int currentLevel = unlockedPlantsAndTheirLevels.get(plant);
        unlockedPlantsAndTheirLevels.put(plant, currentLevel + 1);
    }
}
