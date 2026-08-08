package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

public interface GreenHouseMenuView extends BaseView {
    void showGreenhouseStatus(String status);

    void showPlantPlanted(String plantName, int x, int y);

    void showCollectedMarigold(int coins);

    void showCollectedBoost(String plantName);

    void showAlreadyHasBoost(String plantName);

    void showPotCleared();

    void showGrowthAccelerated();
}
