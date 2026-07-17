package View.ConcreteViews;

import View.ViewInterfaces.GamePlayMenuView;

import java.util.Scanner;

public class GamePlayMenuTerminalView extends AbstractTerminalView implements GamePlayMenuView {

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
