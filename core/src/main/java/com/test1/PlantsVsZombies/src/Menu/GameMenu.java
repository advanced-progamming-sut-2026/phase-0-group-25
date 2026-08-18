package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.Chapter;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.ChapterFactory;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieStats;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GameMenuView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class GameMenu extends Menu {
    private final GameMenuView chapterSelectionView;
    private GameMenuView activeView;
    private GameMenuView levelSelectionView;
    private Chapter chapter;
    private ArrayList<String> plantsStr;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);
        this.chapterSelectionView = gameMenuView;
        this.activeView = gameMenuView;
        this.plantsStr = new ArrayList<>();
        addChangeableMenuType(MenuType.Collection);
        addChangeableMenuType(MenuType.ChoosePlant);
    }

    public void setLevelSelectionView(GameMenuView levelSelectionView) {
        this.levelSelectionView = levelSelectionView;
    }

    /**
     * Currently stored greenhouse/choose-plant boosts, read live from the
     * logged-in user's UserProgress (the single source of truth for boosts
     * now -- see UserProgressManager.takeAndClearGreenhouseBoosts).
     */
    public Set<String> getBoostedPlants() {
        Set<String> result = new HashSet<>();
        User user = UsersManager.getInstance().getLoggedInUser();
        if (user != null) {
            for (PlantType type : user.getUserProgress().getGreenhouseBoosts()) {
                result.add(type.getName());
            }
        }
        return result;
    }

    public ArrayList<String> getPlantsStr() {
        return plantsStr;
    }

    public Chapter getChapter() {
        return chapter;
    }

    private void cheat(int amount, String walletTypeName) {
        WalletType walletType = WalletType.getByName(walletTypeName);
        if (walletType == null) {
            getView().showError("Invalid wallet type.");
            return;
        }
        String error = UsersManager.getInstance().cheat(amount, walletType);
        if (error != null) {
            getView().showError(error);
        }
    }

    @Override
    public void onEnter() {
        activeView = chapterSelectionView;
    }

    public void enterChapter(String chapterName) {
        ChapterType chapterType = ChapterType.getByName(chapterName);
        if (chapterType == null) {
            getView().showError("Invalid chapter name.");
            return;
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null
            || !currentUser.getUserProgress().getUnlockedChaptersAndLevels().containsKey(chapterType)) {
            getView().showError("This chapter is locked.");
            return;
        }

        this.chapter = ChapterFactory.generateChapter(chapterType);
        chapterSelectionView.showChapterEnterSuccess(chapterType.getName());

        if (levelSelectionView != null) {
            activeView = levelSelectionView;
        }
        if (levelSelectionView instanceof com.badlogic.gdx.Screen) {
            com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager
                .changeScreen((com.badlogic.gdx.Screen) levelSelectionView);
        }
    }

    /**
     * Starts the last unlocked level of the current chapter directly --
     * what the "Let's Rock" button on the Choose Plant screen calls,
     * equivalent to clicking that level's island on the level-select screen.
     */
    public void startGame() {
        if (this.chapter == null) {
            getView().showError("You must enter a chapter first.");
            return;
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }
        ChapterType chapterType = this.chapter.getChapterType();
        int lastCompletedLevel = currentUser.getUserProgress()
            .getUnlockedChaptersAndLevels()
            .getOrDefault(chapterType, 0);
        int maxPlayableLevel = Math.min(lastCompletedLevel + 1, ChapterType.LEVELS_PER_CHAPTER);
        startGame(maxPlayableLevel);
    }

    public void startGame(int requestedLevel) {
        if (this.chapter == null) {
            getView().showError("You must enter a chapter first.");
            return;
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        if (
            this.plantsStr == null
                || this.plantsStr.isEmpty()
        ) {
            getView().showError(
                "No plants selected! Please select plants in choose plant menu first."
            );
            return;
        }

        if (
            requestedLevel < 1
                || requestedLevel > ChapterType.LEVELS_PER_CHAPTER
        ) {
            getView().showError(
                "We only have "
                    + ChapterType.LEVELS_PER_CHAPTER
                    + " levels."
            );
            return;
        }

        ChapterType chapterType = this.chapter.getChapterType();
        int lastCompletedLevel = currentUser.getUserProgress()
            .getUnlockedChaptersAndLevels()
            .getOrDefault(chapterType, 0);

        int maxPlayableLevel = Math.min(lastCompletedLevel + 1, ChapterType.LEVELS_PER_CHAPTER);
        if (requestedLevel > maxPlayableLevel) {
            getView().showError("This level is locked. You must beat level " + (requestedLevel - 1) + " first.");
            return;
        }

        ArrayList<String> zombiesStrToPlay = new ArrayList<>();
        String chapterNameStr = chapterType.getName().toLowerCase();
        for (ZombieType zombieType : ZombieType.values()) {
            ZombieStats stats = GameDataLoader.getStatsForZombie(zombieType.getName());
            if (stats != null && stats.getCategory() != null) {
                String categoryStr = stats.getCategory().toLowerCase();
                if (categoryStr.equals("all") || categoryStr.equals(chapterNameStr)) {
                    zombiesStrToPlay.add(zombieType.getName());
                    if (!currentUser.getUserProgress().getUnlockedZombies().contains(zombieType)) {
                        UsersManager.getInstance().unlockZombie(zombieType);
                    }
                }
            }
        }

        // Snapshot the currently stored boosts for THIS level session and
        // clear the persisted list -- see UserProgressManager.takeAndClearGreenhouseBoosts.
        Set<PlantType> boostSnapshot = UsersManager.getInstance().takeAndClearGreenhouseBoosts();
        Set<String> boostedNames = new HashSet<>();
        for (PlantType type : boostSnapshot) {
            boostedNames.add(type.getName());
        }

        GamePlay gamePlay = this.chapter.makeGame(
            requestedLevel,
            currentUser.getUserProgress().getGameDifficulty(),
            currentUser,
            plantsStr,
            zombiesStrToPlay,
            boostedNames
        );

        if (gamePlay == null) {
            getView().showError("Failed to initialize game play.");
            return;
        }

        MenuManager.getInstance().getGamePlayMenu().startSession(gamePlay);
        MenuManager.getInstance().changeMenu(MenuType.GamePlay);
    }



    @Override
    public BaseView getView() {
        return activeView;
    }
}
