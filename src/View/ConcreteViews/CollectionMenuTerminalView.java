package src.View.ConcreteViews;

import src.View.ViewInterfaces.CoinWalletMenuView;
import src.View.ViewInterfaces.CollectionMenuView;

import java.util.Scanner;

public class CollectionMenuTerminalView extends AbstractTerminalView implements CollectionMenuView {


    @Override
    public void showCurrentMenu() {
        System.out.println("collection menu");
    }

    @Override
    public void showPlants() {

    }

    @Override
    public void showAllPlants() {

    }

    @Override
    public void showAllZombies() {

    }

    @Override
    public void showZombies() {

    }

    @Override
    public void showOnePlant() {

    }

    @Override
    public void showOneZombie() {

    }
}
