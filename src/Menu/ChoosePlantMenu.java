package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Enums.PlantType;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ChoosePlantMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

public class ChoosePlantMenu extends Menu {
    private static final int DEFAULT_MAX_PLANTS = 8;
    private final ChoosePlantMenuView choosePlantMenuView;
    private final ArrayList<String> plantsStr;

    private final Set<String> boostedPlants;

    public ChoosePlantMenu(ChoosePlantMenuView view, ArrayList<String> plants, Set<String> boostedPlants) {
        super(MenuType.Game);
        this.plantsStr = plants;
        this.choosePlantMenuView = view;
        this.boostedPlants = boostedPlants;
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

        if ((matcher = getMatcher(input, Command.BoostPlant)) != null) {
            String plantName = matcher.group(1);
            boostPlant(plantName);
            return;
        }


        getView().showError("Invalid command format for this menu state.");
    }

    private void boostPlant(String plantName) {
        PlantType plant = PlantType.fromName(plantName);
        if (plant == null) {
            getView().showError("Invalid plant type.");
            return;
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || !currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels().containsKey(plant)) {
            getView().showError("Plant " + plantName + " is not unlocked.");
            return;
        }
        String gemError = UsersManager.getInstance().subtractGems(2);
        if (gemError != null) {
            getView().showError(gemError);
            return;
        }
        boostedPlants.add(plantName.toUpperCase());
        choosePlantMenuView.showPlantBoosted(plantName);
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

        for (String plantStr : plantsStr) {
            if (plantStr.equalsIgnoreCase(plantType.getName())) {
                getView().showError("Plant is already selected: " + plantType.getName());
                return;
            }
        }

        if (plantsStr.size() >= DEFAULT_MAX_PLANTS) {
            getView().showError("Cannot add more plants. Maximum limit of " + DEFAULT_MAX_PLANTS + " plants reached.");
            return;
        }

        plantsStr.add(plantType.getName());
        choosePlantMenuView.showPlantAddedSuccess(plantType.getName());
    }

    public void removePlant(String typeName) {
        PlantType plantType = PlantType.fromName(typeName);
        if (plantType == null) {
            getView().showError("Plant type not found: " + typeName);
            return;
        }

        String plantToRemove = null;
        for (String plantStr : plantsStr) {
            if (plantStr.equalsIgnoreCase(plantType.getName())) {
                plantToRemove = plantStr;
                break;
            }
        }

        if (plantToRemove == null) {
            getView().showError("Plant is not currently selected: " + plantType.getName());
            return;
        }

        plantsStr.remove(plantToRemove);
        choosePlantMenuView.showPlantRemovedSuccess(plantType.getName());
    }

    @Override
    public BaseView getView() {
        return choosePlantMenuView;
    }
}