package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.CoinWalletMenuView;

public class CoinWalletMenu extends Menu{
    private final CoinWalletMenuView coinWalletMenuView;

    public CoinWalletMenu(CoinWalletMenuView coinWalletMenuView) {
        super(MenuType.Game);
        this.coinWalletMenuView = coinWalletMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return coinWalletMenuView;
    }
}
