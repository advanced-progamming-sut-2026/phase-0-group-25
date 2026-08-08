package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GemWalletMenuView;

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
