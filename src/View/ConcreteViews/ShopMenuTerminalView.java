package src.View.ConcreteViews;

import src.View.ViewInterfaces.ShopMenuView;

import java.util.Scanner;

public class ShopMenuTerminalView extends AbstractTerminalView implements ShopMenuView {

    @Override
    public void showShopList() {

    }

    @Override
    public void showCurrentMenu() {
        System.out.println("shop menu");
    }
}
