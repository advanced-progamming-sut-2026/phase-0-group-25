package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.MainMenuView;

public class MainMenu extends Menu{
    private final MainMenuView mainMenuView;

    public MainMenu(MainMenuView mainMenuView) {
        super(null);
        this.mainMenuView = mainMenuView;
        addChangeableMenuType(MenuType.Game);
        addChangeableMenuType(MenuType.Setting);
        addChangeableMenuType(MenuType.Network);
        addChangeableMenuType(MenuType.News);
        addChangeableMenuType(MenuType.Profile);
    }

    @Override
    public void exit() {
        MenuManager.getInstance().setMustExit();
    }

    @Override
    public void processCommand(String input) {

    }

    public void logOut(){

    }


}
