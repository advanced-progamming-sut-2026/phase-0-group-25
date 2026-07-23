// src/Model/User/UserProgress.java
package src.Model.User;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.ZombieType;
import src.Model.Greenhouse.GreenhousePlant;

import java.time.LocalDate;
import java.util.*;
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProgress {
    private HashMap<ChapterType, Integer> unlockedChaptersAndLevels;
    private ArrayList<ZombieType> unlockedZombies;
    private HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels;
    private int gemsCount;
    private int coinsCount;
    private int gameDifficulty;
    private int gamesPlayed;

    private Map<String, Integer> questProgress;        // questId -> current progress
    private List<String> completedQuestIds;            // quests that are completed but not yet claimed
    private List<String> claimedQuestIds;              // quests already claimed
    private LocalDate lastDailyReset;

    private int plantFoodCount;
    private Map<PlantType, Integer> seedPackets;
    private LocalDate dailyOfferPurchaseDate;

    // Greenhouse – now using 2D arrays
    private boolean[][] unlockedPots;          // [y][x] (y=0..3, x=0..4)
    private GreenhousePlant[][] potPlants;     // [y][x]
    private Set<PlantType> greenhouseBoosts;

    public UserProgress() {
        this.unlockedChaptersAndLevels = new HashMap<>();
        this.unlockedZombies = new ArrayList<>();
        this.unlockedPlantsAndTheirLevels = new HashMap<>();

        this.gamesPlayed = 0;
        this.gemsCount = 0;
        this.coinsCount = 0;
        this.gameDifficulty = 3;

        this.plantFoodCount = 0;
        this.seedPackets = new HashMap<>();
        this.dailyOfferPurchaseDate = null;

        // Initialize greenhouse arrays
        this.unlockedPots = new boolean[4][5];
        this.potPlants = new GreenhousePlant[4][5];
        this.greenhouseBoosts = new HashSet<>();

        // Default: row 1 (index 0) all unlocked
        for (int x = 0; x < 5; x++) {
            unlockedPots[0][x] = true;
        }

        this.questProgress = new HashMap<>();
        this.completedQuestIds = new ArrayList<>();
        this.claimedQuestIds = new ArrayList<>();
        this.lastDailyReset = null;
    }

    public Map<String, Integer> getQuestProgress() { return questProgress; }
    public void setQuestProgress(Map<String, Integer> questProgress) { this.questProgress = questProgress; }

    public List<String> getCompletedQuestIds() { return completedQuestIds; }
    public void setCompletedQuestIds(List<String> completedQuestIds) { this.completedQuestIds = completedQuestIds; }

    public List<String> getClaimedQuestIds() { return claimedQuestIds; }
    public void setClaimedQuestIds(List<String> claimedQuestIds) { this.claimedQuestIds = claimedQuestIds; }

    public LocalDate getLastDailyReset() { return lastDailyReset; }
    public void setLastDailyReset(LocalDate lastDailyReset) { this.lastDailyReset = lastDailyReset; }

    // ----- Getters / Setters for JSON serialisation -----
    public boolean[][] getUnlockedPots() { return unlockedPots; }
    public void setUnlockedPots(boolean[][] unlockedPots) { this.unlockedPots = unlockedPots; }

    public GreenhousePlant[][] getPotPlants() { return potPlants; }
    public void setPotPlants(GreenhousePlant[][] potPlants) { this.potPlants = potPlants; }

    public Set<PlantType> getGreenhouseBoosts() { return greenhouseBoosts; }
    public void setGreenhouseBoosts(Set<PlantType> greenhouseBoosts) { this.greenhouseBoosts = greenhouseBoosts; }

    // ----- Pot count (computed) -----
    public int getPotsCount() {
        int count = 0;
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (unlockedPots[y][x]) count++;
            }
        }
        return count;
    }

    // ----- Package‑private mutators for Greenhouse (called by UsersManager) -----
    void unlockPot(int x, int y) {
        if (x < 1 || x > 5 || y < 1 || y > 4) return;
        unlockedPots[y-1][x-1] = true;
    }

    void unlockNextPot() {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (!unlockedPots[y][x]) {
                    unlockedPots[y][x] = true;
                    return;
                }
            }
        }
    }

    void plantInPot(int x, int y, GreenhousePlant plant) {
        if (x < 1 || x > 5 || y < 1 || y > 4) return;
        potPlants[y-1][x-1] = plant;
    }

    void removePlantFromPot(int x, int y) {
        if (x < 1 || x > 5 || y < 1 || y > 4) return;
        potPlants[y-1][x-1] = null;
    }

    void addGreenhouseBoost(PlantType plant) { greenhouseBoosts.add(plant); }
    boolean hasGreenhouseBoost(PlantType plant) { return greenhouseBoosts.contains(plant); }
    void consumeGreenhouseBoost(PlantType plant) { greenhouseBoosts.remove(plant); }

    // ----- Other fields: getters/setters (unchanged) -----
    public int getPlantFoodCount() { return plantFoodCount; }
    public void setPlantFoodCount(int count) { this.plantFoodCount = Math.min(count, 3); }

    public Map<PlantType, Integer> getSeedPackets() { return seedPackets; }
    public void setSeedPackets(Map<PlantType, Integer> seedPackets) { this.seedPackets = seedPackets; }
    public void addSeedPackets(PlantType plant, int amount) {
        seedPackets.put(plant, seedPackets.getOrDefault(plant, 0) + amount);
    }

    public LocalDate getDailyOfferPurchaseDate() { return dailyOfferPurchaseDate; }
    public void setDailyOfferPurchaseDate(LocalDate date) { this.dailyOfferPurchaseDate = date; }
    public boolean isDailyOfferBoughtToday() {
        return dailyOfferPurchaseDate != null && dailyOfferPurchaseDate.equals(LocalDate.now());
    }

    public HashMap<ChapterType, Integer> getUnlockedChaptersAndLevels() { return unlockedChaptersAndLevels; }
    public void setUnlockedChaptersAndLevels(HashMap<ChapterType, Integer> unlockedChaptersAndLevels) {
        this.unlockedChaptersAndLevels = unlockedChaptersAndLevels;
    }

    public int getGamesPlayed() { return gamesPlayed; }
    void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public ArrayList<ZombieType> getUnlockedZombies() { return unlockedZombies; }
    void setUnlockedZombies(ArrayList<ZombieType> unlockedZombies) { this.unlockedZombies = unlockedZombies; }

    public HashMap<PlantType, Integer> getUnlockedPlantsAndTheirLevels() { return unlockedPlantsAndTheirLevels; }
    public void setUnlockedPlantsAndTheirLevels(HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels) {
        this.unlockedPlantsAndTheirLevels = unlockedPlantsAndTheirLevels;
    }

    public int extractTotalLevelsPassed() {
        int total = 0;
        for (Integer level : unlockedChaptersAndLevels.values()) {
            total += (level - 1);
        }
        return total;
    }

    public int getGemsCount() { return gemsCount; }
    public void setGemsCount(int gemsCount) { this.gemsCount = gemsCount; }

    public int getCoinsCount() { return coinsCount; }
    public void setCoinsCount(int coinsCount) { this.coinsCount = coinsCount; }

    void addCoins(int amount) {
        if (amount > 0) this.coinsCount += amount;
    }
    void addGems(int amount) {
        if (amount > 0) this.gemsCount += amount;
    }

    void setGameDifficulty(int gameDifficulty) { this.gameDifficulty = gameDifficulty; }
    public int getGameDifficulty() { return gameDifficulty; }

    public int getPlantLevel(PlantType plantType) {
        return unlockedPlantsAndTheirLevels.getOrDefault(plantType, 0);
    }

    void unlockPlant(PlantType plantType) { unlockedPlantsAndTheirLevels.put(plantType, 1); }
    void unlockZombie(ZombieType zombieType) { unlockedZombies.add(zombieType); }
    void unlockChapter(ChapterType chapterType) { unlockedChaptersAndLevels.put(chapterType, 1); }
    void unlockLevel(int level, ChapterType chapterType) { unlockedChaptersAndLevels.put(chapterType, level); }

    // ----- Currency subtraction (no exceptions) -----
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

    // ----- Seed packet and upgrade (no exceptions) -----
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