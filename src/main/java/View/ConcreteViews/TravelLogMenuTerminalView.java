package View.ConcreteViews;

import View.ViewInterfaces.TravelLogMenuView;

import java.util.Scanner;

public class TravelLogMenuTerminalView extends AbstractTerminalView implements TravelLogMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("travel log menu");
    }
}
