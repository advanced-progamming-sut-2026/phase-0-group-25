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
    private final GameMenuView gameMenuView;
    private final Set<String> boostedPlants;
    private Chapter chapter;
    private ArrayList<String> plantsStr;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);
        this.gameMenuView = gameMenuView;
        this.plantsStr = new ArrayList<>();
        this.boostedPlants = new HashSet<>();
        addChangeableMenuType(MenuType.Collection);
        addChangeableMenuType(MenuType.ChoosePlant);
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

    private void enterChapter(String chapterName) {
        ChapterType chapterType = ChapterType.getByName(chapterName);
        if (chapterType == null) {
            getView().showError("Invalid chapter name.");
            return;
        }

        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null ||
                !currentUser.getUserProgress().getUnlockedChaptersAndLevels().keySet().contains(chapterType)) {
            getView().showError("This chapter is locked.");
            return;
        }

        this.chapter = ChapterFactory.generateChapter(chapterType);
        gameMenuView.showChapterEnterSuccess(chapterType.getName());
    }

    private void startGame(int requestedLevel) {
        if (this.chapter == null) {
            getView().showError("You must enter a chapter first using 'menu enter chapter -c <chapter_name>'.");
            return;
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }
        if (this.plantsStr == null || this.plantsStr.isEmpty()) {
            getView().showError("No plants selected! Please select plants in choose plant menu first.");
            return;
        }


        if (requestedLevel < 1 || requestedLevel > 4) {
            getView().showError("We only have 4 levels.");
            return;
        }

        ChapterType chapterType = this.chapter.getChapterType();
        int maxUnlockedLevelForChapter = currentUser.getUserProgress().getUnlockedChaptersAndLevels().getOrDefault(chapterType, 1);


        if (requestedLevel > maxUnlockedLevelForChapter) {
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

        GamePlay gamePlay = this.chapter.makeGame(
                requestedLevel,
                currentUser.getUserProgress().getGameDifficulty(),
                currentUser,
                plantsStr,
                zombiesStrToPlay,
                boostedPlants
        );

        if (gamePlay == null) {
            getView().showError("Failed to initialize game play.");
            return;
        }

        GamePlayMenu.setGamePlay(gamePlay);
        MenuManager.getInstance().changeMenu(MenuType.GamePlay);
    }

    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.EnterChapter)) != null) {
            enterChapter(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.StartGame)) != null) {
            int level = Integer.parseInt(matcher.group(1));
            startGame(level);
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterGreenHouse)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.GreenHouse);
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterTravelLog)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.TravelLog);
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterLeaderBoard)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.LeaderBoard);
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterCoinWallet)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.CoinWallet);
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterGemWallet)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.GemWallet);
            return;
        }

        if ((matcher = getMatcher(input, Command.Cheat)) != null) {
            cheat(Integer.parseInt(matcher.group(1)), matcher.group(2));
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return gameMenuView;
    }
}
