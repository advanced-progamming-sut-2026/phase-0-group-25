package com.test1.PlantsVsZombies.src.Model.User;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenhousePlant;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserProgressManager {

    private static final int PLANT_PURCHASE_COST = 2000;
    private static UserProgressManager instance;
    private static int maxLevel = 4;

    public static int getPlantPurchaseCost() {
        return PLANT_PURCHASE_COST;
    }

    public static int getMaxPlantLevel() {
        return maxLevel;
    }

    /**
     * Coins required to upgrade a plant FROM currentLevel to currentLevel+1.
     * Shared by upgradePlant() (enforcement) and the Collection screen
     * (display), so the two can't drift apart.
     */
    public static int getRequiredCoinsForUpgrade(int currentLevel) {
        return currentLevel * 1000;
    }

    /**
     * Seed packets required to upgrade a plant FROM currentLevel to
     * currentLevel+1. Shared by upgradePlant() (enforcement) and the
     * Collection screen (display).
     */
    public static int getRequiredSeedPacketsForUpgrade(int currentLevel) {
        return currentLevel * 5;
    }

    private UserProgressManager() {
    }

    public static UserProgressManager getInstance() {
        if (instance == null) {
            instance = new UserProgressManager();
        }
        return instance;
    }

    private User getLoggedInUser() {
        return UsersManager.getInstance().getLoggedInUser();
    }

    private void save() {
        UsersManager.getInstance().updateUser();
    }

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

    public void addSeedPackets(PlantType plant, int amount) {
        User user = getLoggedInUser();
        if (user == null || amount <= 0) return;
        user.getUserProgress().addSeedPackets(plant, amount);
        save();
    }

    public void setQuestVariablesForCurrentUser(Map<String, String> variables) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.getUserProgress().setQuestVariables(variables);
        save();
    }

    public int getMiniGameLevel(MiniGameType type) {
        User user = getLoggedInUser();
        if (user == null) return 1;
        return user.getUserProgress().getMiniGameLevel(type);
    }

    public void handleMiniGameWin(MiniGameType type, int levelCompleted) {
        User user = getLoggedInUser();
        if (user == null) return;
        UserProgress progress = user.getUserProgress();

        int currentLevel = progress.getMiniGameLevel(type);
        if (levelCompleted >= currentLevel && currentLevel < 3) {
            progress.getMiniGameLevels().put(type, currentLevel + 1);
        }

        progress.incrementMiniGamesCompleted();
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

    public void markLevelCompleted(ChapterType chapterType, int level) {
        User user = getLoggedInUser();
        if (user == null) return;
        user.markLevelCompleted(level, chapterType);
        save();
    }

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

        if (currentLevel == maxLevel)
            return "already at max level.";

        int requiredCoins = getRequiredCoinsForUpgrade(currentLevel);
        int requiredSeedPackets = getRequiredSeedPacketsForUpgrade(currentLevel);

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

    /**
     * Takes a snapshot of the currently stored greenhouse/choose-plant
     * boosts and clears the persisted list in one step. Called exactly
     * once, right when a level actually starts (GameMenu.startGame) --
     * the returned snapshot is what gets handed to that GamePlay session,
     * so boosts don't leak into whatever the user does afterward, and
     * don't get lost if they never place the boosted plant.
     */
    public Set<PlantType> takeAndClearGreenhouseBoosts() {
        User user = getLoggedInUser();
        if (user == null) return new HashSet<>();
        Set<PlantType> liveBoosts = user.getUserProgress().getGreenhouseBoosts();
        Set<PlantType> snapshot = new HashSet<>(liveBoosts);
        liveBoosts.clear();
        save();
        return snapshot;
    }

    public void acceleratePlant(int x, int y) {
        User user = getLoggedInUser();
        if (user == null) return;
        GreenhousePlant plant = user.getUserProgress().getPotPlants()[y - 1][x - 1];
        if (plant != null) {
            plant.forceReady();
            save();
        }
    }


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


    public void handleLevelWin(ChapterType chapterType, int currentLevel,
                               ArrayList<PlantType> plantRewards) {
        User user = getLoggedInUser();
        if (user == null) return;

        UserProgress progress = user.getUserProgress();
        int lastCompletedLevel = progress.getUnlockedChaptersAndLevels()
            .getOrDefault(chapterType, 0);

        // Only advance progress the first time this level is beaten
        // (replaying an already-completed level shouldn't regress it).
        if (currentLevel > lastCompletedLevel) {
            markLevelCompleted(chapterType, currentLevel);

            if (currentLevel == ChapterType.LEVELS_PER_CHAPTER) {
                ChapterType nextChapter = getNextChapter(chapterType);
                if (nextChapter != null) {
                    unlockChapter(nextChapter);
                }
            }
        }

        if (plantRewards != null) {
            for (PlantType pt : plantRewards) unlockPlant(pt);
        }

        progress.addGamesPlayed();
        save();
    }

    public void addGamesPlayed(){
        getLoggedInUser().getUserProgress().addGamesPlayed();
        save();
    }

    private ChapterType getNextChapter(ChapterType current) {
        switch (current) {
            case ANCIENT_EGYPT:
                return ChapterType.DARK_AGE;
            case DARK_AGE:
                return ChapterType.FROSTBITE_CAVES;
            case FROSTBITE_CAVES:
                return ChapterType.BIG_WAVE_BEACH;
            default:
                return null;
        }
    }
}
