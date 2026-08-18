package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.ChoosePlantMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class ChoosePlantMenu extends Menu {
    private static final int DEFAULT_MAX_PLANTS = 8;
    private final ChoosePlantMenuView choosePlantMenuView;
    private final ArrayList<String> plantsStr;

    public ChoosePlantMenu(ChoosePlantMenuView view, ArrayList<String> plants) {
        super(MenuType.Game);
        this.plantsStr = plants;
        this.choosePlantMenuView = view;
    }

    public static int getMaxSelectedPlants() {
        return DEFAULT_MAX_PLANTS;
    }

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
            String plantName = matcher.group(1).trim();
            String error = addPlant(plantName);
            if (error != null) {
                getView().showError(error);
            } else {
                choosePlantMenuView.showPlantAddedSuccess(plantName);
            }
            return;
        }

        if ((matcher = getMatcher(input, Command.ChooseRemovePlant)) != null) {
            String plantName = matcher.group(1).trim();
            String error = removePlant(plantName);
            if (error != null) {
                getView().showError(error);
            } else {
                choosePlantMenuView.showPlantRemovedSuccess(plantName);
            }
            return;
        }

        if ((matcher = getMatcher(input, Command.BoostPlant)) != null) {
            String plantName = matcher.group(1);
            String error = boostPlant(plantName);
            if (error != null) {
                getView().showError(error);
            } else {
                choosePlantMenuView.showPlantBoosted(plantName);
            }
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    // ------------------------------------------------------------
    // Each of these mutates state and returns null on success, or a
    // user-facing error message on failure. handleSpecificCommands(...)
    // routes the result to the terminal view above; CollectionMenuScreen's
    // sibling, ChoosePlantScreen, calls these same methods directly for
    // the GUI, so the validation logic lives in exactly one place.
    // ------------------------------------------------------------

    public String addPlant(String typeName) {
        PlantType plantType = PlantType.fromName(typeName);
        if (plantType == null) {
            return "Plant type not found: " + typeName;
        }

        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            return "No logged in user found.";
        }

        Map<PlantType, Integer> unlockedMap = currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels();
        if (!unlockedMap.containsKey(plantType)) {
            return "Plant is locked: " + plantType.getName();
        }

        for (String plantStr : plantsStr) {
            if (plantStr.equalsIgnoreCase(plantType.getName())) {
                return "Plant is already selected: " + plantType.getName();
            }
        }

        if (plantsStr.size() >= DEFAULT_MAX_PLANTS) {
            return "Cannot add more plants. Maximum limit of " + DEFAULT_MAX_PLANTS + " plants reached.";
        }

        plantsStr.add(plantType.getName());
        return null;
    }

    public String removePlant(String typeName) {
        PlantType plantType = PlantType.fromName(typeName);
        if (plantType == null) {
            return "Plant type not found: " + typeName;
        }

        String plantToRemove = null;
        for (String plantStr : plantsStr) {
            if (plantStr.equalsIgnoreCase(plantType.getName())) {
                plantToRemove = plantStr;
                break;
            }
        }

        if (plantToRemove == null) {
            return "Plant is not currently selected: " + plantType.getName();
        }

        plantsStr.remove(plantToRemove);
        return null;
    }

    /**
     * Stores a boost for this plant (2 gems), same underlying storage as
     * GreenHouse boosting -- see UserProgressManager.addGreenhouseBoost /
     * takeAndClearGreenhouseBoosts.
     */
    public String boostPlant(String plantName) {
        PlantType plant = PlantType.fromName(plantName);
        if (plant == null) {
            return "Invalid plant type.";
        }
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || !currentUser.getUserProgress().getUnlockedPlantsAndTheirLevels().containsKey(plant)) {
            return "Plant " + plantName + " is not unlocked.";
        }
        if (UsersManager.getInstance().hasGreenhouseBoost(plant)) {
            return "You already have a boost stored for " + plantName + ".";
        }
        String gemError = UsersManager.getInstance().subtractGems(2);
        if (gemError != null) {
            return gemError;
        }
        UsersManager.getInstance().addGreenhouseBoost(plant);
        return null;
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

    /**
     * The plants currently selected for the next game (shared, same list
     * reference GameMenu holds -- adding/removing here is reflected there
     * too, since they're literally the same ArrayList).
     */
    public ArrayList<String> getSelectedPlants() {
        return plantsStr;
    }

    @Override
    public BaseView getView() {
        return choosePlantMenuView;
    }
}
