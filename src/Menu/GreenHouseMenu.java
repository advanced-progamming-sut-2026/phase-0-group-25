
package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Enums.PlantType;
import src.Model.Greenhouse.GreenhousePlant;
import src.Model.User.UserProgress;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GreenHouseMenuView;

import java.util.Random;
import java.util.regex.Matcher;

public class GreenHouseMenu extends Menu {
    private final GreenHouseMenuView greenHouseMenuView;

    public GreenHouseMenu(GreenHouseMenuView greenHouseMenuView) {
        super(MenuType.Game);
        this.greenHouseMenuView = greenHouseMenuView;
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
        UsersManager um = UsersManager.getInstance();
        UserProgress progress = um.getLoggedInUser().getUserProgress();
        boolean[][] unlocked = progress.getUnlockedPots();
        GreenhousePlant[][] plants = progress.getPotPlants();

        StringBuilder sb = new StringBuilder();
        sb.append("=== Greenhouse ===\n");
        sb.append("(Rows 1-4, Columns 1-5)\n");
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 5; x++) {
                if (!unlocked[y][x]) {
                    sb.append("[LOCKED] ");
                    continue;
                }
                GreenhousePlant plant = plants[y][x];
                if (plant == null) {
                    sb.append("[EMPTY] ");
                } else if (plant.isReady()) {
                    sb.append("[READY] ").append(plant.getType().getName()).append(" ");
                } else {
                    double rem = plant.getRemainingHours();
                    sb.append("[").append(plant.getType().getName())
                            .append(" ").append(String.format("%.1f", rem)).append("h] ");
                }
            }
            sb.append("\n");
        }
        greenHouseMenuView.showGreenhouseStatus(sb.toString());
    }

    private void plantPot(int x, int y) {
        if (x < 1 || x > 5 || y < 1 || y > 4) {
            getView().showError("Invalid coordinates. Use x=1-5, y=1-4.");
            return;
        }
        UsersManager um = UsersManager.getInstance();
        UserProgress progress = um.getLoggedInUser().getUserProgress();
        boolean[][] unlocked = progress.getUnlockedPots();
        GreenhousePlant[][] plants = progress.getPotPlants();

        if (!unlocked[y - 1][x - 1]) {
            getView().showError("Pot is locked.");
            return;
        }
        if (plants[y - 1][x - 1] != null) {
            getView().showError("Pot is occupied.");
            return;
        }

        Random rand = new Random();
        PlantType chosen;
        if (rand.nextBoolean()) {
            chosen = PlantType.MARIGOLD;
        } else {
            var unlockedPlants = progress.getUnlockedPlantsAndTheirLevels().keySet();
            if (unlockedPlants.isEmpty()) {
                getView().showError("No plants unlocked to plant.");
                return;
            }
            int idx = rand.nextInt(unlockedPlants.size());
            chosen = (PlantType) unlockedPlants.toArray()[idx];
        }

        double growthHours = (chosen == PlantType.MARIGOLD) ? 2 : 8;
        GreenhousePlant plant = new GreenhousePlant(chosen, growthHours);
        um.plantInPot(x, y, plant);
        greenHouseMenuView.showPlantPlanted(chosen.getName(), x, y);
    }

    private void collect(int x, int y) {
        UsersManager um = UsersManager.getInstance();
        UserProgress progress = um.getLoggedInUser().getUserProgress();
        GreenhousePlant[][] plants = progress.getPotPlants();
        GreenhousePlant plant = plants[y - 1][x - 1];
        if (plant == null) {
            getView().showError("No plant in this pot.");
            return;
        }
        if (!plant.isReady()) {
            getView().showError("Plant is not ready yet.");
            return;
        }

        um.removePlantFromPot(x, y);

        if (plant.getType() == PlantType.MARIGOLD) {
            um.addCoins(500);
            greenHouseMenuView.showCollectedMarigold(500);
        } else {
            if (um.hasGreenhouseBoost(plant.getType())) {
                greenHouseMenuView.showAlreadyHasBoost(plant.getType().getName());
            } else {
                um.addGreenhouseBoost(plant.getType());
                greenHouseMenuView.showCollectedBoost(plant.getType().getName());
            }
        }
    }

    private void grow(int x, int y) {
        UsersManager um = UsersManager.getInstance();
        UserProgress progress = um.getLoggedInUser().getUserProgress();
        GreenhousePlant[][] plants = progress.getPotPlants();
        GreenhousePlant plant = plants[y - 1][x - 1];
        if (plant == null) {
            getView().showError("No plant in this pot.");
            return;
        }
        if (plant.isReady()) {
            getView().showError("Plant is already ready.");
            return;
        }

        double remainingHours = plant.getRemainingHours();
        int gemsNeeded = (int) Math.ceil(remainingHours);
        if (gemsNeeded <= 0) {
            getView().showError("Plant is already ready.");
            return;
        }

        String error = um.subtractGems(gemsNeeded);
        if (error != null) {
            getView().showError(error);
            return;
        }

        um.acceleratePlant(x, y);
        greenHouseMenuView.showGrowthAccelerated();
    }

    @Override
    public BaseView getView() {
        return greenHouseMenuView;
    }
}