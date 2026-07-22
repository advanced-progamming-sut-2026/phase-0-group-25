package src.View.ViewInterfaces;

import java.util.List;

public interface CollectionMenuView extends BaseView {

    void showPlants(List<String> plantNames);

    void showAllPlants(List<String> plantNames);

    void showZombies(List<String> zombieNames);

    void showAllZombies(List<String> zombieNames);

    void showPlantDetails(String plantName, int cost, int baseHP);

    void showZombieDetails(String zombieName, double velocity, int baseHP);

    void showPlantPurchased(String plantName);
}
