package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.MainMenuView;

public class MainMenu extends Menu{
    private final MainMenuView mainMenuView;

    public MainMenu(MainMenuView mainMenuView) {
        super(MenuType.Signup);
        this.mainMenuView = mainMenuView;
        addChangeableMenuType(MenuType.Game);
        addChangeableMenuType(MenuType.Setting);
        addChangeableMenuType(MenuType.Network);
        addChangeableMenuType(MenuType.News);
        addChangeableMenuType(MenuType.Profile);
    }





    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return mainMenuView;
    }

    public void logOut(){

    }


}
