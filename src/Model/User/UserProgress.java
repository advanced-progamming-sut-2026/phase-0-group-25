package src.Model.User;

import src.Enums.ChapterType;
import src.Enums.PlantType;
import src.Enums.ZombieType;
import src.Model.ChaptersAndLevels.Chapter;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class UserProgress {
    private ArrayList<ChapterType> unlockedChapters;
    private ArrayList<ZombieType> unlockedZombies;
    private ArrayList<PlantType> unlockedPlants;
    private int gemsCount;
    private int coinsCount;

    public UserProgress() {
        this.unlockedChapters = new ArrayList<>();
        this.unlockedZombies = new ArrayList<>();
        this.unlockedPlants = new ArrayList<>();

        this.unlockedChapters.add(ChapterType.ANCIENT_EGYPT);

        this.gemsCount = 0;
        this.coinsCount = 0;
    }

    public ArrayList<ChapterType> getUnlockedChapters() {
        return unlockedChapters;
    }

    public void setUnlockedChapters(ArrayList<ChapterType> unlockedChapters) {
        this.unlockedChapters = unlockedChapters;
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

    public void addGems(int amount) {
        if (amount > 0) {
            this.gemsCount += amount;
        }
    }

}
