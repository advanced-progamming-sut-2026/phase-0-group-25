package src.View.ConcreteViews;

import src.View.ViewInterfaces.GameMenuView;

import java.util.Scanner;

public class GameMenuTerminalView  implements GameMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("game menu");
    }
}
