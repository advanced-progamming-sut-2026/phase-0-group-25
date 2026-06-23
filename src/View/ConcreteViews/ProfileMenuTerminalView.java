package src.View.ConcreteViews;

import src.View.ViewInterfaces.ProfileMenuView;

import java.util.Scanner;

public class ProfileMenuTerminalView extends AbstractTerminalView implements ProfileMenuView {

    @Override
    public void showInfo() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("profile menu");
    }
}
