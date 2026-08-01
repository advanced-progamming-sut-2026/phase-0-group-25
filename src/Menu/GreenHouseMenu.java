package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.Greenhouse.GreenHouseManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GreenHouseMenuView;

import java.util.regex.Matcher;

public class GreenHouseMenu extends Menu {
    private final GreenHouseMenuView greenHouseMenuView;
    private final GreenHouseManager manager;

    public GreenHouseMenu(GreenHouseMenuView greenHouseMenuView) {
        super(MenuType.Game);
        this.greenHouseMenuView = greenHouseMenuView;
        this.manager = GreenHouseManager.getInstance();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.EnterShop)) != null) {
            MenuManager.getInstance().changeMenu(MenuType.Shop);
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowGreenhouse)) != null) {
            showGreenhouse();
            return;
        }

        if ((matcher = getMatcher(input, Command.PlantPot)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            plantPot(x, y);
            return;
        }

        if ((matcher = getMatcher(input, Command.CollectPot)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            collect(x, y);
            return;
        }

        if ((matcher = getMatcher(input, Command.GrowPot)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            grow(x, y);
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }


    private void showGreenhouse() {
        String status = manager.getGreenhouseStatus();
        greenHouseMenuView.showGreenhouseStatus(status);
    }

    private void plantPot(int x, int y) {
        String result = manager.plantPot(x, y);
        if (result.startsWith("Planted")) {

            String plantName = result.substring(result.indexOf("Planted") + 8, result.indexOf(" in pot")).trim();
            greenHouseMenuView.showPlantPlanted(plantName, x, y);
        } else {
            getView().showError(result);
        }
    }

    private void collect(int x, int y) {
        String result = manager.collectPot(x, y);
        if (result.startsWith("Collected")) {
            if (result.contains("Marigold")) {
                greenHouseMenuView.showCollectedMarigold(500);
            } else if (result.contains("boost stored")) {
                String plantName = result.substring(result.indexOf("Collected") + 10, result.indexOf(" ->")).trim();
                greenHouseMenuView.showCollectedBoost(plantName);
            } else if (result.contains("already have a boost")) {
                String plantName = result.substring(result.indexOf("for ") + 4, result.indexOf(". Pot cleared")).trim();
                greenHouseMenuView.showAlreadyHasBoost(plantName);
            } else {

                getView().showError(result);
            }
        } else {
            getView().showError(result);
        }
    }

    private void grow(int x, int y) {
        String result = manager.growPot(x, y);
        if (result == null) {
            greenHouseMenuView.showGrowthAccelerated();
        } else {
            getView().showError(result);
        }
    }

    @Override
    public BaseView getView() {
        return greenHouseMenuView;
    }
}