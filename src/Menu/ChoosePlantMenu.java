package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Enums.PlantType;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.PlantFactory;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ChoosePlantMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class ChoosePlantMenu extends Menu {
    private static final int DEFAULT_MAX_PLANTS = 8;
    private final ChoosePlantMenuView choosePlantMenuView;
    private final ArrayList<Plant> plants;
    private final PlantFactory plantFactory;

    public ChoosePlantMenu(ChoosePlantMenuView choosePlantMenuView, ArrayList<Plant> plants) {
        super(MenuType.Game);
        this.plants = plants;
        this.choosePlantMenuView = choosePlantMenuView;
        this.plantFactory = new PlantFactory();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.ChooseShowAllPlants)) != null) {
            showAllPlants();
            return;
        }

        if ((matcher = getMatcher(input, Command.ChooseShowAvailablePlants)) != null) {
            showAvailablePlants();
            return;
        }

        if ((matcher = getMatcher(input, Command.ChooseAddPlant)) != null) {
            addPlant(matcher.group(1).trim());
            return;
        }

        if ((matcher = getMatcher(input, Command.ChooseRemovePlant)) != null) {
            removePlant(matcher.group(1).trim());
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    public void showAllPlants() {
        List<String> plantNames = new ArrayList<>();
        for (PlantType plantType : PlantType.values()) {
            plantNames.add(plantType.getName());
        }
        choosePlantMenuView.showAllPlants(plantNames);
    }

    public void showAvailablePlants() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        Map<PlantType, Integer> unlockedMap = currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
        List<String> availablePlantNames = new ArrayList<>();
        for (PlantType plantType : unlockedMap.keySet()) {
            availablePlantNames.add(plantType.getName());
        }
        choosePlantMenuView.showAvailablePlants(availablePlantNames);
    }

    public void addPlant(String typeName) {
        PlantType plantType = PlantType.fromName(typeName);
        if (plantType == null) {
            getView().showError("Plant type not found: " + typeName);
            return;
        }

        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }

        Map<PlantType, Integer> unlockedMap = currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
        if (!unlockedMap.containsKey(plantType)) {
            getView().showError("Plant is locked: " + plantType.getName());
            return;
        }

        for (Plant plant : plants) {
            if (plant.getName().equalsIgnoreCase(plantType.getName())) {
                getView().showError("Plant is already selected: " + plantType.getName());
                return;
            }
        }

        if (plants.size() >= DEFAULT_MAX_PLANTS) {
            getView().showError("Cannot add more plants. Maximum limit of " + DEFAULT_MAX_PLANTS + " plants reached.");
            return;
        }

        int level = unlockedMap.get(plantType);
        BattlePlant newPlant = plantFactory.createBattlePlant(plantType.getName(), level);
        plants.add(newPlant);
        choosePlantMenuView.showPlantAddedSuccess(plantType.getName());
    }

    public void removePlant(String typeName) {
        PlantType plantType = PlantType.fromName(typeName);
        if (plantType == null) {
            getView().showError("Plant type not found: " + typeName);
            return;
        }

        Plant plantToRemove = null;
        for (Plant plant : plants) {
            if (plant.getName().equalsIgnoreCase(plantType.getName())) {
                plantToRemove = plant;
                break;
            }
        }

        if (plantToRemove == null) {
            getView().showError("Plant is not currently selected: " + plantType.getName());
            return;
        }

        plants.remove(plantToRemove);
        choosePlantMenuView.showPlantRemovedSuccess(plantType.getName());
    }

    @Override
    public BaseView getView() {
        return choosePlantMenuView;
    }
}