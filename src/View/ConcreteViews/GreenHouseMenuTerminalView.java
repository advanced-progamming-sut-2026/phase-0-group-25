package src.View.ConcreteViews;

import src.View.ViewInterfaces.GreenHouseMenuView;

import java.util.Scanner;

public class GreenHouseMenuTerminalView extends AbstractTerminalView implements GreenHouseMenuView {

    @Override
    public void ShowGreenHouse() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("greenhouse menu");
    }
}
