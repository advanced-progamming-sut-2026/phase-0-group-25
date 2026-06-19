package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.MainMenuView;

public class MainMenu extends Menu{
    private MainMenuView mainMenuView;

    public MainMenu(MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        addChangeableMenuType(MenuType.Game);
        addChangeableMenuType(MenuType.Setting);
        addChangeableMenuType(MenuType.Network);
        addChangeableMenuType(MenuType.News);
        addChangeableMenuType(MenuType.Profile);
    }

    public void logOut(){

    }
}
