package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.LoginMenuView;

public class LoginMenu extends Menu{
    private LoginMenuView loginMenuView;

    public LoginMenu(LoginMenuView loginMenuView) {
        this.loginMenuView = loginMenuView;
        addChangeableMenuType(MenuType.Main);
    }

    public void login(){

    }
    public void forgotPassword(){

    }
}
