package Menu;

import Enums.MenuType;
import Model.User.User;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.CoinWalletMenuView;

public class CoinWalletMenu extends Menu{
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
