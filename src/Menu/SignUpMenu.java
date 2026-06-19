package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.SignUpMenuView;

public class SignUpMenu extends Menu{
    private SignUpMenuView signUpMenuView;

    public SignUpMenu(SignUpMenuView signUpMenuView) {
        this.signUpMenuView = signUpMenuView;
        addChangeableMenuType(MenuType.Login);
    }

    public void registerUser(){

    }
}
