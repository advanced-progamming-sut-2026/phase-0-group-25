package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Enums.PlantType;
import src.Enums.ZombieType;
import src.Model.PlantsAndZombies.*;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.CollectionMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class CollectionMenu extends Menu {
    private static final int PLANT_PURCHASE_COST = 2000;

    private final CollectionMenuView collectionMenuView;
    private ZombieFactory zombieFactory;

    public CollectionMenu(CollectionMenuView collectionMenuView) {
        super(MenuType.Game);
        this.collectionMenuView = collectionMenuView;
    }

    public void setZombieFactory(ZombieFactory zombieFactory) {
        this.zombieFactory = zombieFactory;
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.ShowPlants)) != null) {
            showAcquiredPlants();
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowAllPlants)) != null) {
            showAllPlants();
            return;
        }
        if ((matcher = getMatcher(input, Command.UpgradePlant)) != null) {
            String plantName = matcher.group(1);
            String error = UsersManager.getInstance().upgradePlant(plantName);
            if (error == null) {
                collectionMenuView.showPlantUpgradeSuccess(plantName);
            } else {
                getView().showError(error);
            }
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowZombies)) != null) {
            showAcquiredZombies();
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowAllZombies)) != null) {
            showAllZombies();
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowPlantDetails)) != null) {
            showPlantDetails(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowZombieDetails)) != null) {
            showZombieDetails(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.PurchasePlant)) != null) {
            purchasePlant(matcher.group(1));
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    private void showAcquiredPlants() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        Map<PlantType, Integer> unlockedPlantsMap = currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
        List<String> plantNames = new ArrayList<>();
        for (PlantType type : unlockedPlantsMap.keySet()) {
            plantNames.add(type.getName());
        }

        collectionMenuView.showPlants(plantNames);
    }

    private void showAllPlants() {
        List<String> plantNames = new ArrayList<>();
        for (PlantType plantType : PlantType.values()) {
            plantNames.add(plantType.getName());
        }

        collectionMenuView.showAllPlants(plantNames);
    }

    private void showAcquiredZombies() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        List<ZombieType> unlockedZombies = currentUser.getUserProgress().getUnlockedZombies();
        List<String> zombieNames = new ArrayList<>();
        for (ZombieType type : unlockedZombies) {
            zombieNames.add(type.getName());
        }

        collectionMenuView.showZombies(zombieNames);
    }

    private void showAllZombies() {
        List<String> zombieNames = new ArrayList<>();
        for (ZombieType zombieType : ZombieType.values()) {
            zombieNames.add(zombieType.getName());
        }

        collectionMenuView.showAllZombies(zombieNames);
    }

    private void showPlantDetails(String plantName) {
        PlantType plantType = PlantType.fromName(plantName);
        if (plantType == null) {
            getView().showError("Plant not found!");
            return;
        }

        int level = 1;
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser != null && currentUser.getUserProgress() != null) {
            Map<PlantType, Integer> unlockedMap = currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
            if (unlockedMap.containsKey(plantType)) {
                level = unlockedMap.get(plantType);
            }
        }

        BattlePlant plant = PlantFactory.createBattlePlant(plantType.getName(), level);
        if (plant == null || plant.getPlantStats() == null) {
            getView().showError("Plant stats not found for: " + plantName);
            return;
        }

        PlantStats stats = plant.getPlantStats();
        collectionMenuView.showPlantDetails(plantType.getName(), stats.getCost(), stats.getBaseHP());
    }

    private void showZombieDetails(String zombieName) {
        ZombieType zombieType;
        try {
            zombieType = ZombieType.fromName(zombieName);
        } catch (IllegalArgumentException e) {
            getView().showError("Zombie not found!");
            return;
        }

        if (zombieFactory != null) {
            Zombie zombie = ZombieFactory.createZombie(zombieType.getName());
            if (zombie.getZombieStats() != null) {
                ZombieStats stats = zombie.getZombieStats();
                collectionMenuView.showZombieDetails(stats.getName(), zombie.getZombieStats().getVelocity(), stats.getBaseHP());
                return;
            }
        }

        collectionMenuView.showZombieDetails(zombieType.getName(), 0.0, 0);
    }

    private void purchasePlant(String plantName) {
        String error = UsersManager.getInstance().purchasePlant(plantName);
        if (error != null) {
            getView().showError(error);
            return;
        }

        PlantType plantType = PlantType.fromName(plantName);
        collectionMenuView.showPlantPurchased(plantType != null ? plantType.getName() : plantName);
    }

    @Override
    public BaseView getView() {
        return collectionMenuView;
    }
}