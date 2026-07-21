// file: src/Menu/GameMenu.java
package src.Menu;

import src.Enums.Command;
import src.Enums.ChapterType;
import src.Enums.MenuType;
import src.Enums.WalletType;
import src.Model.ChaptersAndLevels.Chapter;
import src.Model.ChaptersAndLevels.ChapterFactory;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Plant;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GameMenuView;

import java.util.ArrayList;
import java.util.regex.Matcher;

public class GameMenu extends Menu {
    private final GameMenuView gameMenuView;
    private Chapter chapter;
    private ArrayList<Plant> plants;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);
        this.gameMenuView = gameMenuView;
        this.plants = new ArrayList<>();
        addChangeableMenuType(MenuType.Collection);
        addChangeableMenuType(MenuType.ChoosePlant);
    }

    public ArrayList<Plant> getPlants() {
        return plants;
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

    private void enterChapter(String chapterName){
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

    private void startGame() {
        if (this.chapter == null) {
            getView().showError("You must enter a chapter first using 'menu enter chapter -c <chapter_name>'.");
            return;
        }

        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        if (this.plants == null || this.plants.isEmpty()) {
            getView().showError("No plants selected! Please select plants in choose plant menu first.");
            return;
        }

        ChapterType chapterType = this.chapter.getChapterType();
        int unlockedLevel = currentUser.getUserProgress().getUnlockedChaptersAndLevels().getOrDefault(chapterType, 1);

        if (unlockedLevel > 4) {
            getView().showError("All levels of this chapter have already been completed!");
            return;
        }

        ArrayList<BattlePlant> battlePlants = new ArrayList<>();
        for (Plant plant : this.plants) {
            if (plant instanceof BattlePlant) {
                battlePlants.add((BattlePlant) plant);
            }
        }

        GamePlay gamePlay = this.chapter.makeGame(
                unlockedLevel,
                currentUser.getUserProgress().getGameDifficulty(),
                currentUser,
                battlePlants
        );

        if (gamePlay == null) {
            getView().showError("Failed to initialize game play.");
            return;
        }

        GamePlayMenu gamePlayMenu = (GamePlayMenu) MenuManager.getInstance().getMenu(MenuType.GamePlay);
        gamePlayMenu.setGamePlay(gamePlay);
        MenuManager.getInstance().changeMenu(MenuType.GamePlay);
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.EnterChapter)) != null) {
            enterChapter(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.StartGame)) != null) {
            startGame();
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
