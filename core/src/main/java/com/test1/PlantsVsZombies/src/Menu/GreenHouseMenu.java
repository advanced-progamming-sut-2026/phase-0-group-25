
package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.Greenhouse.GreenHouseManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GreenHouseMenuView;

import java.util.regex.Matcher;

public class GreenHouseMenu extends Menu {
    private final GreenHouseMenuView greenHouseMenuView;
    private final GreenHouseManager manager;

    public GreenHouseMenu(GreenHouseMenuView greenHouseMenuView) {
        super(MenuType.Game);
        this.greenHouseMenuView = greenHouseMenuView;
        this.manager = GreenHouseManager.getInstance();
    }

    public void handleSpecificCommands(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.EnterShop)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.Shop);
            return;
        }
        if ((matcher = getMatcher(input, Command.ShowGreenhouse)) != null) {
            String status = manager.getGreenhouseStatus();
            greenHouseMenuView.showGreenhouseStatus(status);
            return;
        }
        getView().showError("Invalid command format for this menu state.");
    }


    public String buyPot(int x, int y) {
        return manager.unlockPot(x, y);
    }

    public String plantPot(int x, int y) {
        return manager.plantPot(x, y);
    }

    public String collectPot(int x, int y) {
        return manager.collectPot(x, y);
    }

    public String growPot(int x, int y) {
        return manager.growPot(x, y);
    }

    @Override
    public BaseView getView() {
        return greenHouseMenuView;
    }
}
