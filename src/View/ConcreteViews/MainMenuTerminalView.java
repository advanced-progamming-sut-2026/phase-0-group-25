package src.View.ConcreteViews;

import src.View.ViewInterfaces.MainMenuView;

import java.util.Scanner;

public class MainMenuTerminalView extends AbstractTerminalView implements MainMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("main menu");
    }
}
