package src.View.ConcreteViews;

import src.View.ViewInterfaces.GameMenuView;

import java.util.Scanner;

public class GameMenuTerminalView extends AbstractTerminalView implements GameMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("game menu");
    }
}
