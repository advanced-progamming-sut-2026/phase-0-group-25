package src.View.ConcreteViews;

import src.View.ViewInterfaces.CoinWalletMenuView;

public class CoinWalletMenuTerminalView extends AbstractTerminalView implements CoinWalletMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("coin wallet menu");
    }

    @Override
    public void showCoinsCount(int coinsCount) {
        System.out.println("Coins: " + coinsCount);
    }
}
