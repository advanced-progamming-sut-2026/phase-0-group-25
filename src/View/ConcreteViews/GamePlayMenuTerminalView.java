package src.View.ConcreteViews;

import src.View.ViewInterfaces.GamePlayMenuView;

import java.util.Scanner;

public class GamePlayMenuTerminalView implements GamePlayMenuView {

    @Override
    public void showTileStatus() {

    }

    @Override
    public void showPlantStatus() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("game play menu");
    }
}
