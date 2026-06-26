package src.Model.User;

import src.Model.ChaptersAndLevels.Chapter;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class UserProgress {
    private ArrayList<Chapter> unlockedChapters;
    private ArrayList<src.Model.PlantsAndZombies.Zombie> unlockedZombies;
    private ArrayList<src.Model.PlantsAndZombies.Plant> unlockedPlants;
    private int gemsCount;
    private int coinsCount;

    public UserProgress() {
    }

    public ArrayList<Chapter> getUnlockedChapters() {
        return unlockedChapters;
    }

    public void setUnlockedChapters(ArrayList<Chapter> unlockedChapters) {
        this.unlockedChapters = unlockedChapters;
    }

    public ArrayList<Zombie> getUnlockedZombies() {
        return unlockedZombies;
    }

    public void setUnlockedZombies(ArrayList<Zombie> unlockedZombies) {
        this.unlockedZombies = unlockedZombies;
    }

    public ArrayList<Plant> getUnlockedPlants() {
        return unlockedPlants;
    }

    public void setUnlockedPlants(ArrayList<Plant> unlockedPlants) {
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
}
