package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.LoginMenuView;

public class LoginMenu extends Menu{
    private final LoginMenuView loginMenuView;

    public LoginMenu(LoginMenuView loginMenuView) {
        super(MenuType.Signup);
        this.loginMenuView = loginMenuView;
        addChangeableMenuType(MenuType.Main);
    }

    public void login(){

    }
    public void forgotPassword(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return loginMenuView;
    }
}
