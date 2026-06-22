package src.View.ConcreteViews;

import src.View.ViewInterfaces.CoinWalletMenuView;

import java.util.Scanner;

public class CoinWalletMenuTerminalView implements CoinWalletMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("coin wallet menu");
    }
}
