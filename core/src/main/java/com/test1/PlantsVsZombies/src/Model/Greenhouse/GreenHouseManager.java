package com.test1.PlantsVsZombies.src.Model.Greenhouse;

import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

import java.util.Random;
import java.util.Set;

public class GreenHouseManager {
    private static GreenHouseManager instance;
    private final UsersManager usersManager;

    private GreenHouseManager() {
        usersManager = UsersManager.getInstance();
    }

    public static GreenHouseManager getInstance() {
        if (instance == null) {
            instance = new GreenHouseManager();
        }
        return instance;
    }


    private String getUserError() {
        if (usersManager.getLoggedInUser() == null) {
            return "No logged in user.";
        }
        return null;
    }


    public String getGreenhouseStatus() {
        String error = getUserError();
        if (error != null) return error;

        var progress = usersManager.getLoggedInUser().getUserProgress();
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
        return sb.toString();
    }


    public String plantPot(int x, int y) {
        String error = getUserError();
        if (error != null) return error;

        if (x < 1 || x > 5 || y < 1 || y > 4) {
            return "Invalid coordinates. Use x=1-5, y=1-4.";
        }

        UserProgress progress = usersManager.getLoggedInUser().getUserProgress();
        boolean[][] unlocked = progress.getUnlockedPots();
        GreenhousePlant[][] plants = progress.getPotPlants();

        if (!unlocked[y - 1][x - 1]) {
            return "Pot is locked.";
        }
        if (plants[y - 1][x - 1] != null) {
            return "Pot is occupied.";
        }

        Random rand = new Random();
        PlantType chosen;
        if (rand.nextBoolean()) {
            chosen = PlantType.MARIGOLD;
        } else {
            Set<PlantType> unlockedPlants = progress.getUnlockedPlantsAndTheirLevels().keySet();
            if (unlockedPlants.isEmpty()) {
                return "No plants unlocked to plant.";
            }
            int idx = rand.nextInt(unlockedPlants.size());
            chosen = (PlantType) unlockedPlants.toArray()[idx];
        }

        double growthHours = (chosen == PlantType.MARIGOLD) ? 2 : 8;
        GreenhousePlant plant = new GreenhousePlant(chosen, growthHours);
        usersManager.plantInPot(x, y, plant);
        return "Planted " + chosen.getName() + " in pot (" + x + "," + y + ").";
    }


    public String collectPot(int x, int y) {
        String error = getUserError();
        if (error != null) return error;

        if (x < 1 || x > 5 || y < 1 || y > 4) {
            return "Invalid coordinates.";
        }

        UserProgress progress = usersManager.getLoggedInUser().getUserProgress();
        GreenhousePlant[][] plants = progress.getPotPlants();
        GreenhousePlant plant = plants[y - 1][x - 1];

        if (plant == null) {
            return "No plant in this pot.";
        }
        if (!plant.isReady()) {
            return "Plant is not ready yet.";
        }

        usersManager.removePlantFromPot(x, y);

        if (plant.getType() == PlantType.MARIGOLD) {
            usersManager.addCoins(500);
            return "Collected Marigold: +500 coins.";
        } else {
            if (usersManager.hasGreenhouseBoost(plant.getType())) {
                return "You already have a boost for " + plant.getType().getName() + ". Pot cleared.";
            } else {
                usersManager.addGreenhouseBoost(plant.getType());
                return "Collected " + plant.getType().getName() + " -> greenhouse boost stored.";
            }
        }
    }


    public String growPot(int x, int y) {
        String error = getUserError();
        if (error != null) return error;

        if (x < 1 || x > 5 || y < 1 || y > 4) {
            return "Invalid coordinates.";
        }

        UserProgress progress = usersManager.getLoggedInUser().getUserProgress();
        GreenhousePlant[][] plants = progress.getPotPlants();
        GreenhousePlant plant = plants[y - 1][x - 1];

        if (plant == null) {
            return "No plant in this pot.";
        }
        if (plant.isReady()) {
            return "Plant is already ready.";
        }

        double remainingHours = plant.getRemainingHours();
        int gemsNeeded = (int) Math.ceil(remainingHours);
        if (gemsNeeded <= 0) {
            return "Plant is already ready.";
        }

        String gemError = usersManager.subtractGems(gemsNeeded);
        if (gemError != null) {
            return gemError;
        }

        usersManager.acceleratePlant(x, y);
        return null;
    }
}
