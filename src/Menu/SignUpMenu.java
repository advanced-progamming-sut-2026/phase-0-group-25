package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.SignUpMenuView;

import java.util.regex.Matcher;

public class SignUpMenu extends Menu{
    private final SignUpMenuView signUpMenuView;

    public SignUpMenu(SignUpMenuView signUpMenuView) {
        super(null);
        this.signUpMenuView = signUpMenuView;
        addChangeableMenuType(MenuType.Login);
    }

    @Override
    public void exit() {
        MenuManager.getInstance().setMustExit();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.RegisterAccount)) != null){

            return;
        }

    }

    @Override
    public BaseView getView() {
        return signUpMenuView;
    }

    public void registerUser(){

    }
}
