package src.Model.User;

import src.Enums.*;
import src.Model.Greenhouse.GreenhousePlant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Handles all modifications to the currently logged-in user's progress.
 * Every method persists changes to the JSON file via UsersManager.updateUser().
 */
public class UserProgressManager {

    private static UserProgressManager instance;

    private static int maxLevel = 4;

    private UserProgressManager() {}

    public static UserProgressManager getInstance() {
        if (instance == null) {
            instance = new UserProgressManager();
        }
        return instance;
    }

    // ----- Helper to get current user and save after modification -----
    private User getLoggedInUser() {
        return UsersManager.getInstance().getLoggedInUser();
    }

    private void save() {
        UsersManager.getInstance().updateUser();
    }

    // ========== Currency ==========
    public void addCoins(int amount) {
        User user = getLoggedInUser();
        if (user == null || amount <= 0) return;
        user.getUserProgress().addCoins(amount);
        save();
    }

    public void addGems(int amount) {
        User user = getLoggedInUser();
        if (user == null || amount <= 0) return;
        user.getUserProgress().addGems(amount);
        save();
    }

    public String subtractCoins(int amount) {
        User user = getLoggedInUser();
        if (user == null) return "No logged in user.";
        UserProgress progress = user.getUserProgress();
        if (amount < 0) return "Cannot subtract negative amount.";
        if (progress.getCoinsCount() < amount)
            return "Insufficient coins. You have " + progress.getCoinsCount() + ", need " + amount + ".";
        progress.subtractCoins(amount);
        save();
        return null;
    }

    public String subtractGems(int amount) {
        User user = getLoggedInUser();
        if (user == null) return "No logged in user.";
        UserProgress progress = user.getUserProgress();
        if (amount < 0) return "Cannot subtract negative amount.";
        if (progress.getGemsCount() < amount)
            return "Insufficient gems. You have " + progress.getGemsCount() + ", need " + amount + ".";
        progress.subtractGems(amount);
        save();
        return null;
    }

    // ========== Seed Packets & Plant Food ==========
    public void addSeedPackets(PlantType plant, int amount) {
        User user = getLoggedInUser();
        if (user == null || amount <= 0) return;
        user.getUserProgress().addSeedPackets(plant, amount);
        save();
    }

    public void addPlantFood(int amount) {
        User user = getLoggedInUser();
        if (user == null) return;
        UserProgress progress = user.getUserProgress();
        int newCount = Math.max(0, progress.getPlantFoodCount() + amount);
        progress.setPlantFoodCount(newCount);
        save();
    }

    // ========== Unlocks ==========
    public void unlockPlant(PlantType plantType) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.unlockPlant(plantType);
        save();
    }

    public void unlockZombie(ZombieType zombieType) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.unlockZombie(zombieType);
        save();
    }

    public void unlockChapter(ChapterType chapterType) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.unlockChapter(chapterType);
        save();
    }

    public void unlockLevel(ChapterType chapterType, int level) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.unlockLevel(level, chapterType);
        save();
    }

    // ========== Plant Purchase & Upgrade ==========
    private static final int PLANT_PURCHASE_COST = 2000;

    public String purchasePlant(String plantName) {
        User user = getLoggedInUser();
        if (user == null) return "No logged in user.";

        PlantType plantType = PlantType.fromName(plantName);
        if (plantType == null) return "Plant not found!";

        UserProgress progress = user.getUserProgress();
        if (progress.getUnlockedPlantsAndTheirLevels().containsKey(plantType))
            return "You already own this plant!";

        String error = subtractCoins(PLANT_PURCHASE_COST);
        if (error != null) return error;

        unlockPlant(plantType);
        return null;
    }

    public String upgradePlant(String plantName) {
        User user = getLoggedInUser();
        if (user == null) return "No logged in user.";

        PlantType plant = PlantType.fromName(plantName);
        if (plant == null) return "Invalid plant name.";

        UserProgress progress = user.getUserProgress();
        if (!progress.getUnlockedPlantsAndTheirLevels().containsKey(plant))
            return "Plant not unlocked.";

        int currentLevel = progress.getUnlockedPlantsAndTheirLevels().get(plant);

        if(currentLevel == maxLevel)
            return "already at max level.";

        int requiredCoins = currentLevel * 1000;
        int requiredSeedPackets = currentLevel * 5;

        if (progress.getCoinsCount() < requiredCoins)
            return "Insufficient coins. Need " + requiredCoins + ".";
        if (!progress.hasEnoughSeedPackets(plant, requiredSeedPackets)) {
            int available = progress.getSeedPackets().getOrDefault(plant, 0);
            return "Not enough seed packets. Need " + requiredSeedPackets + ", have " + available + ".";
        }

        progress.subtractCoins(requiredCoins);
        progress.deductSeedPackets(plant, requiredSeedPackets);
        progress.upgradePlant(plant);
        save();
        return null;
    }

    // ========== Greenhouse ==========
    public void unlockPot(int x, int y) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().unlockPot(x, y);
        save();
    }

    public void addPots(int amount) {
        User user = getLoggedInUser();
        if (user == null || amount <= 0) return;
        UserProgress progress = user.getUserProgress();
        for (int i = 0; i < amount; i++) {
            progress.unlockNextPot();
        }
        save();
    }

    public void plantInPot(int x, int y, GreenhousePlant plant) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().plantInPot(x, y, plant);
        save();
    }

    public void removePlantFromPot(int x, int y) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().removePlantFromPot(x, y);
        save();
    }

    public void addGreenhouseBoost(PlantType plant) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().addGreenhouseBoost(plant);
        save();
    }

    public boolean hasGreenhouseBoost(PlantType plant) {
        User user = getLoggedInUser();
        if (user == null) return false;
        return user.getUserProgress().hasGreenhouseBoost(plant);
    }

    public void consumeGreenhouseBoost(PlantType plant) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().consumeGreenhouseBoost(plant);
        save();
    }

    public void acceleratePlant(int x, int y) {
        User user = getLoggedInUser();
        if (user == null) return;
        GreenhousePlant plant = user.getUserProgress().getPotPlants()[y-1][x-1];
        if (plant != null) {
            plant.forceReady();
            save();
        }
    }

    // ========== Shop Daily Offer ==========
    public void markDailyOfferPurchased() {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setDailyOfferPurchaseDate(LocalDate.now());
        save();
    }

    public boolean isDailyOfferBoughtToday() {
        User user = getLoggedInUser();
        if (user == null) return false;
        return user.getUserProgress().isDailyOfferBoughtToday();
    }

    // ========== Quest Progress (called by QuestManager) ==========
    public void setQuestProgressForCurrentUser(Map<String, Integer> progress) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setQuestProgress(progress);
        save();
    }

    public void setCompletedQuestIdsForCurrentUser(List<String> completed) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setCompletedQuestIds(completed);
        save();
    }

    public void setClaimedQuestIdsForCurrentUser(List<String> claimed) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setClaimedQuestIds(claimed);
        save();
    }

    public void setLastDailyResetForCurrentUser(LocalDate date) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setLastDailyReset(date);
        save();
    }

    // ========== Leaderboard Counters ==========
    public void incrementMiniGamesCompleted() {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().incrementMiniGamesCompleted();
        save();
    }

    public void incrementDailyQuestsCompleted() {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().incrementDailyQuestsCompleted();
        save();
    }

    public void incrementNonDailyQuestsCompleted() {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().incrementNonDailyQuestsCompleted();
        save();
    }

    // ========== Cheat (direct add) ==========
    public String cheat(int amount, WalletType walletType) {
        User user = getLoggedInUser();
        if (user == null || user.getUserProgress() == null)
            return "No logged in user found.";
        if (amount <= 0) return "Cheat amount must be positive.";

        UserProgress progress = user.getUserProgress();
        if (walletType == WalletType.COIN) {
            progress.addCoins(amount);
        } else if (walletType == WalletType.DIAMOND) {
            progress.addGems(amount);
        } else {
            return "Invalid wallet type.";
        }
        save();
        return null;
    }

    // ========== Level Completion ==========
    public void handleLevelWin(ChapterType chapterType, int currentLevel,
                               ArrayList<PlantType> plantRewards,
                               ArrayList<ZombieType> zombieRewards) {
        User user = getLoggedInUser();
        if (user == null) return;

        UserProgress progress = user.getUserProgress();
        int currentUnlockedLevel = progress.getUnlockedChaptersAndLevels()
                .getOrDefault(chapterType, 1);

        if (currentLevel >= currentUnlockedLevel) {
            if (currentLevel < 4) {
                unlockLevel(chapterType, currentLevel + 1);
            } else if (currentLevel == 4) {
                ChapterType nextChapter = getNextChapter(chapterType);
                if (nextChapter != null) {
                    unlockChapter(nextChapter);
                }
            }
        }

        if (plantRewards != null) {
            for (PlantType pt : plantRewards) unlockPlant(pt);
        }
        if (zombieRewards != null) {
            for (ZombieType zt : zombieRewards) unlockZombie(zt);
        }

        progress.setGamesPlayed(progress.getGamesPlayed() + 1);
        save();
    }

    private ChapterType getNextChapter(ChapterType current) {
        switch (current) {
            case ANCIENT_EGYPT: return ChapterType.DARK_AGE;
            case DARK_AGE: return ChapterType.FROSTBITE_CAVES;
            case FROSTBITE_CAVES: return ChapterType.BIG_WAVE_BEACH;
            default: return null;
        }
    }
}