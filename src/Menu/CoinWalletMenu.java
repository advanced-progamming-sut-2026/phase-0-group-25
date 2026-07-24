package src.Menu;

import src.Enums.MenuType;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.CoinWalletMenuView;

public class CoinWalletMenu extends Menu {
    private final CoinWalletMenuView coinWalletMenuView;

    public CoinWalletMenu(CoinWalletMenuView coinWalletMenuView) {
        super(MenuType.Game);
        this.coinWalletMenuView = coinWalletMenuView;
    }

    @Override
    public void onEnter() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }
        coinWalletMenuView.showCoinsCount(currentUser.getUserProgress().getCoinsCount());
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return coinWalletMenuView;
    }
}
