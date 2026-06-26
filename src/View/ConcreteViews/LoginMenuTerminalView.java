package src.View.ConcreteViews;

import src.View.ViewInterfaces.LoginMenuView;

import java.util.Scanner;

public class LoginMenuTerminalView extends AbstractTerminalView implements LoginMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("login menu");
    }
}
