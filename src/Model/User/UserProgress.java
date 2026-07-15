package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.ZombieType;
import src.Model.ChaptersAndLevels.Chapter;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;
import java.util.HashMap;

public class UserProgress {
    private HashMap<ChapterType, Integer> unlockedChaptersAndLevels;
    private ArrayList<ZombieType> unlockedZombies;
    private ArrayList<PlantType> unlockedPlants;
    private int gemsCount;
    private int coinsCount;
    private int gameDifficulty;

    public UserProgress() {
        this.unlockedChaptersAndLevels = new HashMap<>();
        this.unlockedZombies = new ArrayList<>();
        this.unlockedPlants = new ArrayList<>();

        this.unlockedChaptersAndLevels.put(ChapterType.ANCIENT_EGYPT, 1);

        this.gemsCount = 0;
        this.coinsCount = 0;
        this.gameDifficulty = 3;
    }

    public HashMap<ChapterType, Integer> getUnlockedChaptersAndLevels() {
        return unlockedChaptersAndLevels;
    }

    public void setUnlockedChaptersAndLevels(HashMap<ChapterType, Integer> unlockedChaptersAndLevels) {
        this.unlockedChaptersAndLevels = unlockedChaptersAndLevels;
    }

    public ArrayList<ZombieType> getUnlockedZombies() {
        return unlockedZombies;
    }

    public void setUnlockedZombies(ArrayList<ZombieType> unlockedZombies) {
        this.unlockedZombies = unlockedZombies;
    }

    public ArrayList<PlantType> getUnlockedPlants() {
        return unlockedPlants;
    }

    public void setUnlockedPlants(ArrayList<PlantType> unlockedPlants) {
        this.unlockedPlants = unlockedPlants;
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

    public void addCoins(int amount) {
        if (amount > 0) {
            this.coinsCount += amount;
        }
    }

    public void setGameDifficulty(int gameDifficulty) {
        this.gameDifficulty = gameDifficulty;
    }

    public int getGameDifficulty() {
        return gameDifficulty;
    }

    public void addGems(int amount) {
        if (amount > 0) {
            this.gemsCount += amount;
        }
    }

}
