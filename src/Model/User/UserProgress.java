package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.ZombieType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserProgress {
    private HashMap<ChapterType, Integer> unlockedChaptersAndLevels;
    private ArrayList<ZombieType> unlockedZombies;
    private HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels;
    private int gemsCount;
    private int coinsCount;
    private int potsCount;
    private int gameDifficulty;
    private int gamesPlayed;

    private int plantFoodCount;
    private Map<PlantType, Integer> seedPackets;
    private LocalDate dailyOfferPurchaseDate;

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
    }


    public int getPlantFoodCount() { return plantFoodCount; }
    public void setPlantFoodCount(int count) { this.plantFoodCount = Math.min(count, 3); } // enforce max 3

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

    public void setPotsCount(int potsCount) {
        this.potsCount = potsCount;
    }

    public HashMap<ChapterType, Integer> getUnlockedChaptersAndLevels() {
        return unlockedChaptersAndLevels;
    }
// file: src/Model/User/UserProgress.java
// Add these methods inside the class

    public void subtractCoins(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot subtract a negative amount.");
        }
        if (this.coinsCount < amount) {
            throw new IllegalArgumentException("Insufficient coins. You have " + this.coinsCount + ", need " + amount);
        }
        this.coinsCount -= amount;
    }

    public void subtractGems(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot subtract a negative amount.");
        }
        if (this.gemsCount < amount) {
            throw new IllegalArgumentException("Insufficient gems. You have " + this.gemsCount + ", need " + amount);
        }
        this.gemsCount -= amount;
    }

    public void setUnlockedChaptersAndLevels(HashMap<ChapterType, Integer> unlockedChaptersAndLevels) {
        this.unlockedChaptersAndLevels = unlockedChaptersAndLevels;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

     void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
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

    public void setUnlockedPlantsAndTheirLevels(HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels) {
        this.unlockedPlantsAndTheirLevels = unlockedPlantsAndTheirLevels;
    }

    public int extractTotalLevelsPassed() {
        int totalLevels = 0;
        for (Integer level : unlockedChaptersAndLevels.values()) {
            totalLevels += (level - 1);
        }
        return totalLevels;
    }
    public int getGemsCount() {
        return gemsCount;
    }

    public void setGemsCount(int gemsCount) {
        this.gemsCount = gemsCount;
    }

    public int getCoinsCount() {
        return coinsCount;
    }

    public void setCoinsCount(int coinsCount) {
        this.coinsCount = coinsCount;
    }

     void addCoins(int amount) {
        if (amount > 0) {
            this.coinsCount += amount;
        }
    }

     void setGameDifficulty(int gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public int getGameDifficulty() {
        return gameDifficulty;
    }

    public int getPlantLevel(PlantType plantType){
        if(unlockedPlantsAndTheirLevels.keySet().contains(plantType))
            return unlockedPlantsAndTheirLevels.get(plantType);
        return 0;
    }


     void unlockPlant(PlantType plantType){
        unlockedPlantsAndTheirLevels.put(plantType, 1);
    }



     void unlockZombie(ZombieType zombieType){
        unlockedZombies.add(zombieType);
    }



     void unlockChapter(ChapterType chapterType){
        unlockedChaptersAndLevels.put(chapterType, 1);
    }

     void unlockLevel(int level, ChapterType chapterType){
        unlockedChaptersAndLevels.put(chapterType, level);
    }
    void addGems(int amount) {
        if (amount > 0) {
            this.gemsCount += amount;
        }
    }

    public void addPots(int amount) {
        if (amount > 0) {
            this.potsCount += amount;
        }
    }

    public int getPotsCount() {
        return potsCount;
    }
}
