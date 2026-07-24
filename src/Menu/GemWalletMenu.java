package src.Menu;

import src.Enums.MenuType;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GemWalletMenuView;

public class GemWalletMenu extends Menu {

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
