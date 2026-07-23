package src.Model.Greenhouse;

import src.Model.PlantsAndZombies.Plant;

import src.Enums.PlantType;
import java.time.LocalDateTime;

public class GreenhousePlant {
    private PlantType type;
    private LocalDateTime plantedAt;
    private double growthHours; // total hours needed

    public GreenhousePlant(PlantType type, double growthHours) {
        this.type = type;
        this.growthHours = growthHours;
        this.plantedAt = LocalDateTime.now();
    }

    public PlantType getType() { return type; }
    public LocalDateTime getPlantedAt() { return plantedAt; }
    public double getGrowthHours() { return growthHours; }

    /**
     * @return remaining hours (can be negative if ready)
     */
    public double getRemainingHours() {
        long secondsElapsed = java.time.Duration.between(plantedAt, LocalDateTime.now()).getSeconds();
        double hoursElapsed = secondsElapsed / 3600.0;
        return growthHours - hoursElapsed;
    }

    public boolean isReady() {
        return getRemainingHours() <= 0;
    }

    public void forceReady() {
        this.plantedAt = LocalDateTime.now().minusHours((long) growthHours).minusSeconds(1);
    }

}
