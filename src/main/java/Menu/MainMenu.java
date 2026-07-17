package Menu;

import Enums.Command;
import Enums.MenuType;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.MainMenuView;

import java.util.regex.Matcher;

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
        getView().showError("can only go back with the logout command");
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.MenuLogout)) != null) {
            logout();
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return mainMenuView;
    }

    private void logout(){
        UsersManager.getInstance().logoutCurrentUser();
        MenuManager.getInstance().changeMenu(MenuType.Signup);
    }
}
