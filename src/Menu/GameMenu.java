package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GameMenuView;

public class GameMenu extends Menu{
    private final GameMenuView gameMenuView;

    public GameMenu(GameMenuView gameMenuView) {
        super(MenuType.Main);
        this.gameMenuView = gameMenuView;
        addChangeableMenuType(MenuType.Collection);
    }

    public void enterChapter(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return gameMenuView;
    }
}
