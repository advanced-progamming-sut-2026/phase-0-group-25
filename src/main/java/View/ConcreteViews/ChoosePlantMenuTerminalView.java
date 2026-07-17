package View.ConcreteViews;

import View.ViewInterfaces.ChoosePlantMenuView;

import java.util.Scanner;

public class ChoosePlantMenuTerminalView extends AbstractTerminalView implements ChoosePlantMenuView{

    @Override
    public void showAllPlants() {

    }

    @Override
    public void showAvailablePlants() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("choose plant menu");
    }
}
