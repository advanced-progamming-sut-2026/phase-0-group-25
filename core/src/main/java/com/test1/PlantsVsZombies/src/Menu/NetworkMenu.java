package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.NetworkMenuView;

public class NetworkMenu extends Menu {
    private final NetworkMenuView networkMenuView;

    public NetworkMenu(NetworkMenuView networkMenuView) {
        super(MenuType.Main);
        this.networkMenuView = networkMenuView;
    }

    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return networkMenuView;
    }
}
