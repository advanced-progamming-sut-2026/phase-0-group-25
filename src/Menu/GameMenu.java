package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.GameMenuView;

public class GameMenu extends Menu{
    private GameMenuView gameMenuView;

    public GameMenu(GameMenuView gameMenuView) {
        this.gameMenuView = gameMenuView;
        addChangeableMenuType(MenuType.Collection);
    }

    public void enterChapter(){

    }
}
