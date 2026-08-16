package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.Chapter;
import com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels.ChapterFactory;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.Simple;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.GameDataLoader;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieStats;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.GamePlayScreen;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GameMenuView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class GameMenu extends Menu {

    private final GameMenuView chapterSelectionView;

    /*
     * This is the view that currently receives errors and other
     * UI messages.
     *
     * At first it is ChooseChapterScreen.
     * After entering a chapter it becomes GameLevelScreen.
     */
    private GameMenuView activeView;

    private GameMenuView levelSelectionView;

    private final Set<String> boostedPlants;

    private Chapter chapter;

    private ArrayList<String> plantsStr;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);

        this.chapterSelectionView = gameMenuView;
        this.activeView = gameMenuView;

        this.plantsStr = new ArrayList<>();
        this.boostedPlants = new HashSet<>();

        addChangeableMenuType(MenuType.Collection);
        addChangeableMenuType(MenuType.ChoosePlant);
    }

    public void setLevelSelectionView(GameMenuView levelSelectionView) {
        this.levelSelectionView = levelSelectionView;
    }

    public Set<String> getBoostedPlants() {
        return boostedPlants;
    }

    public ArrayList<String> getPlantsStr() {
        return plantsStr;
    }

    public Chapter getChapter() {
        return chapter;
    }

    private void cheat(int amount, String walletTypeName) {
        WalletType walletType =
            WalletType.getByName(walletTypeName);

        if (walletType == null) {
            getView().showError(
                "Invalid wallet type."
            );
            return;
        }

        String error =
            UsersManager.getInstance().cheat(
                amount,
                walletType
            );

        if (error != null) {
            getView().showError(error);
        }
    }

    /*
     * Whenever MenuManager enters MenuType.Game again,
     * it means we are returning to chapter selection.
     */
    @Override
    public void onEnter() {
        activeView = chapterSelectionView;
    }

    public void enterChapter(String chapterName) {

        ChapterType chapterType =
            ChapterType.getByName(chapterName);

        if (chapterType == null) {
            getView().showError(
                "Invalid chapter name."
            );
            return;
        }

        User currentUser =
            UsersManager.getInstance().getLoggedInUser();

        if (
            currentUser == null
                || currentUser.getUserProgress() == null
                || !currentUser
                .getUserProgress()
                .getUnlockedChaptersAndLevels()
                .containsKey(chapterType)
        ) {

            getView().showError(
                "This chapter is locked."
            );

            return;
        }

        /*
         * Create the actual Chapter object.
         *
         * This also gives us all Level objects containing
         * their individual GamePlayType values.
         */
        this.chapter =
            ChapterFactory.generateChapter(chapterType);

        /*
         * Keep the existing chapter-enter toast.
         */
        chapterSelectionView.showChapterEnterSuccess(
            chapterType.getName()
        );

        /*
         * Switch the active GameMenu view to the level-selection
         * screen.
         */
        if (levelSelectionView != null) {
            activeView = levelSelectionView;
        }

        /*
         * MenuManager normally handles Screen changes, but
         * this transition occurs inside GameMenu itself.
         *
         * GameLevelScreen is registered as a LibGDX Screen.
         */
        if (levelSelectionView instanceof com.badlogic.gdx.Screen) {
            com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager
                .changeScreen(
                    (com.badlogic.gdx.Screen) levelSelectionView
                );
        }
    }

    public void startGame(int requestedLevel) {

        if (this.chapter == null) {
            getView().showError(
                "You must enter a chapter first."
            );
            return;
        }

        User currentUser =
            UsersManager.getInstance().getLoggedInUser();

        if (
            currentUser == null
                || currentUser.getUserProgress() == null
        ) {
            getView().showError(
                "No logged in user found."
            );
            return;
        }

//        if (
//            this.plantsStr == null
//                || this.plantsStr.isEmpty()
//        ) {
//            getView().showError(
//                "No plants selected! Please select plants in choose plant menu first."
//            );
//            return;
//        }

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

        ChapterType chapterType =
            this.chapter.getChapterType();

        int lastCompletedLevel =
            currentUser
                .getUserProgress()
                .getUnlockedChaptersAndLevels()
                .getOrDefault(chapterType, 0);

        /*
         * Example:
         *
         * lastCompletedLevel = 0
         * -> level 1 playable
         *
         * lastCompletedLevel = 1
         * -> levels 1 and 2 playable
         *
         * lastCompletedLevel = 2
         * -> levels 1, 2 and 3 playable
         */
        int maxPlayableLevel =
            Math.min(
                lastCompletedLevel + 1,
                ChapterType.LEVELS_PER_CHAPTER
            );

        if (requestedLevel > maxPlayableLevel) {
            getView().showError(
                "This level is locked. You must beat level "
                    + (requestedLevel - 1)
                    + " first."
            );
            return;
        }

        ArrayList<String> zombiesStrToPlay =
            new ArrayList<>();

        String chapterNameStr =
            chapterType.getName().toLowerCase();

        for (ZombieType zombieType : ZombieType.values()) {

            ZombieStats stats =
                GameDataLoader.getStatsForZombie(
                    zombieType.getName()
                );

            if (
                stats != null
                    && stats.getCategory() != null
            ) {

                String categoryStr =
                    stats.getCategory().toLowerCase();

                if (
                    categoryStr.equals("all")
                        || categoryStr.equals(chapterNameStr)
                ) {

                    zombiesStrToPlay.add(
                        zombieType.getName()
                    );

                    if (
                        !currentUser
                            .getUserProgress()
                            .getUnlockedZombies()
                            .contains(zombieType)
                    ) {

                        UsersManager.getInstance()
                            .unlockZombie(zombieType);
                    }
                }
            }
        }

        /*
         * THIS is where the correct gameplay type is selected.
         *
         * ChapterFactory created the Level with its GamePlayType.
         * Chapter.makeGame() gets that Level.
         * Level.createGame() passes the GamePlayType to
         * GamePlayFactory.
         */
        GamePlay gamePlay =
            this.chapter.makeGame(
                requestedLevel,
                currentUser
                    .getUserProgress()
                    .getGameDifficulty(),
                currentUser,
                plantsStr,
                zombiesStrToPlay,
                boostedPlants
            );

        if (gamePlay == null) {
            getView().showError(
                "Failed to initialize game play."
            );
            return;
        }

        GamePlayMenu.setGamePlay(gamePlay);
        MenuManager.getInstance().getGamePlayMenu().setGamePlayMenuView(new GamePlayScreen(gamePlay));

        MenuManager.getInstance()
            .changeMenu(MenuType.GamePlay);
    }

    public void handleSpecificCommands(String input) {

        Matcher matcher;

        if (
            (matcher = getMatcher(
                input,
                Command.EnterChapter
            )) != null
        ) {

            enterChapter(matcher.group(1));
            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.StartGame
            )) != null
        ) {

            int level =
                Integer.parseInt(
                    matcher.group(1)
                );

            startGame(level);
            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.EnterGreenHouse
            )) != null
        ) {

            MenuManager.getInstance()
                .changeMenu(MenuType.GreenHouse);

            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.EnterTravelLog
            )) != null
        ) {

            MenuManager.getInstance()
                .changeMenu(MenuType.TravelLog);

            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.EnterLeaderBoard
            )) != null
        ) {

            MenuManager.getInstance()
                .changeMenu(MenuType.LeaderBoard);

            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.EnterCoinWallet
            )) != null
        ) {

            MenuManager.getInstance()
                .changeMenu(MenuType.CoinWallet);

            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.EnterGemWallet
            )) != null
        ) {

            MenuManager.getInstance()
                .changeMenu(MenuType.GemWallet);

            return;
        }

        if (
            (matcher = getMatcher(
                input,
                Command.Cheat
            )) != null
        ) {

            cheat(
                Integer.parseInt(
                    matcher.group(1)
                ),
                matcher.group(2)
            );

            return;
        }

        getView().showError(
            "Invalid command format for this menu state."
        );
    }

    @Override
    public BaseView getView() {
        return activeView;
    }
}
