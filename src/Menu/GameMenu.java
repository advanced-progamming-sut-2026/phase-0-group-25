package src.Menu;

import src.Enums.Command;
import src.Enums.ChapterType;
import src.Enums.MenuType;
import src.Enums.WalletType;
import src.Model.ChaptersAndLevels.Chapter;
import src.Model.ChaptersAndLevels.ChapterFactory;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GameMenuView;

import java.util.regex.Matcher;

public class GameMenu extends Menu{
    private final GameMenuView gameMenuView;
    private Chapter chapter;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);
        this.gameMenuView = gameMenuView;
        addChangeableMenuType(MenuType.Collection);
    }

    private void cheat(int amount, String walletTypeName){
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
                !currentUser.getUserProgress().getUnlockedChapters().contains(chapterType)) {
            getView().showError("This chapter is locked.");
            return;
        }

        this.chapter = ChapterFactory.generateChapter(chapterType);
        gameMenuView.showChapterEnterSuccess(chapterType.getName());
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.EnterChapter)) != null) {
            enterChapter(matcher.group(1));
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
