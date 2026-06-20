package src.Model.PlantsAndZombies;

public class BattlePlant extends Plant {
    private double lastActionTime;
    private double timeElapsedAsAlive;
    private PlantStats plantStats;

    public BattlePlant(PlantStats plantStats, String name) {
        this.lastActionTime = 0;
        this.timeElapsedAsAlive = 0;
        this.plantStats = plantStats;
        this.name = name;
        this.currentHP = plantStats.getBaseHP();
    }


    @Override
    public void update() {

    }

    public Position getPosition() {
        return position;
    }

    public PlantStats getPlantStats() {
        return plantStats;
    }

    public double getTimeElapsedAsAlive() {
        return timeElapsedAsAlive;
    }
}
