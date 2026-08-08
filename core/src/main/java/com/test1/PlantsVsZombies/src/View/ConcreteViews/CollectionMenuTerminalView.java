package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.View.ViewInterfaces.CollectionMenuView;

import java.util.List;

public class CollectionMenuTerminalView extends AbstractTerminalView implements CollectionMenuView {

    @Override
    public void showCurrentMenu() {
        System.out.println("collection menu");
    }

    @Override
    public void showPlants(List<String> plantNames) {
        System.out.println("--- Acquired Plants ---");
        if (plantNames.isEmpty()) {
            System.out.println("No plants acquired yet.");
            return;
        }
        for (String plant : plantNames) {
            System.out.println("- " + plant);
        }
    }

    @Override
    public void showPlantUpgradeSuccess(String plantName) {
        System.out.println("Plant " + plantName + " upgraded successfully!");
    }

    @Override
    public void showAllPlants(List<String> plantNames) {
        System.out.println("--- All Defined Plants ---");
        for (String plant : plantNames) {
            System.out.println("- " + plant);
        }
    }

    @Override
    public void showZombies(List<String> zombieNames) {
        System.out.println("--- Encountered Zombies ---");
        if (zombieNames.isEmpty()) {
            System.out.println("No zombies encountered yet.");
            return;
        }
        for (String zombie : zombieNames) {
            System.out.println("- " + zombie);
        }
    }

    @Override
    public void showAllZombies(List<String> zombieNames) {
        System.out.println("--- All Defined Zombies ---");
        for (String zombie : zombieNames) {
            System.out.println("- " + zombie);
        }
    }

    @Override
    public void showPlantDetails(String plantName, int cost, int baseHP, String category) {
        System.out.println("Plant Name: " + plantName);
        System.out.println("Category: " + category);
        System.out.println("Cost: " + cost);
        System.out.println("Base HP: " + baseHP);
    }

    @Override
    public void showZombieDetails(String zombieName, double velocity, int baseHP, String category) {
        System.out.println("Zombie Name: " + zombieName);
        System.out.println("Category: " + category);
        System.out.println("Velocity: " + velocity);
        System.out.println("Base HP: " + baseHP);
    }

    @Override
    public void showPlantPurchased(String plantName) {
        System.out.println(plantName + " has been successfully purchased for 2,000 coins!");
    }
}
