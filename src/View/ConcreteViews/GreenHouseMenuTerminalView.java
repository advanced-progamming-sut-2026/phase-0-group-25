package src.View.ConcreteViews;

import src.View.ViewInterfaces.GreenHouseMenuView;

public class GreenHouseMenuTerminalView extends AbstractTerminalView implements GreenHouseMenuView {

    @Override
    public void showGreenhouseStatus(String status) {
        System.out.println(status);
    }

    @Override
    public void showPlantPlanted(String plantName, int x, int y) {
        System.out.println("Planted " + plantName + " in pot (" + x + "," + y + ").");
    }

    @Override
    public void showCollectedMarigold(int coins) {
        System.out.println("Collected Marigold: +" + coins + " coins.");
    }

    @Override
    public void showCollectedBoost(String plantName) {
        System.out.println("Collected " + plantName + " -> greenhouse boost stored.");
    }

    @Override
    public void showAlreadyHasBoost(String plantName) {
        System.out.println("You already have a boost for " + plantName + ". Pot cleared.");
    }

    @Override
    public void showPotCleared() {
        System.out.println("Pot cleared.");
    }

    @Override
    public void showGrowthAccelerated() {
        System.out.println("Plant growth accelerated. It is now ready.");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("greenhouse menu");
    }
}
