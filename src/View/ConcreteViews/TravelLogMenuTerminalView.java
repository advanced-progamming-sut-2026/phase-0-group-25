package src.View.ConcreteViews;

import src.View.ViewInterfaces.TravelLogMenuView;

import java.util.Scanner;

public class TravelLogMenuTerminalView implements TravelLogMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("travel log menu");
    }
}
