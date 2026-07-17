package Menu;

import Enums.MenuType;
import Model.User.User;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.GemWalletMenuView;

public class GemWalletMenu extends Menu{

    private final GemWalletMenuView gemWalletMenuView;

    public GemWalletMenu(GemWalletMenuView gemWalletMenuView) {
        super(MenuType.Game);
        this.gemWalletMenuView = gemWalletMenuView;
    }

    @Override
    public void onEnter() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }
        gemWalletMenuView.showGemsCount(currentUser.getUserProgress().getGemsCount());
    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return gemWalletMenuView;
    }
}
