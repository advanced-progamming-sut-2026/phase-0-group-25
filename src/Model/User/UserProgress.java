package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.ZombieType;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProgress {
    private HashMap<ChapterType, Integer> unlockedChaptersAndLevels;
    private ArrayList<ZombieType> unlockedZombies;
    private HashMap<PlantType, Integer> unlockedPlantsAndTheirLevels;
    private int gemsCount;
    private int coinsCount;
    private int potsCount;
    private int gameDifficulty;
    private int gamesPlayed;
    private int seedPacketCount;

    public UserProgress() {
        this.unlockedChaptersAndLevels = new HashMap<>();
        this.unlockedZombies = new ArrayList<>();
        this.unlockedPlantsAndTheirLevels = new HashMap<>();


        this.gamesPlayed = 0;
        this.gemsCount = 0;
        this.coinsCount = 0;
        this.gameDifficulty = 3;
        this.seedPacketCount = 0;
    }

    public int getSeedPacketCount() {
        return seedPacketCount;
    }

    public void setSeedPacketCount(int seedPacketCount) {
        this.seedPacketCount = seedPacketCount;
    }

    public HashMap<ChapterType, Integer> getUnlockedChaptersAndLevels() {
        return unlockedChaptersAndLevels;
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
