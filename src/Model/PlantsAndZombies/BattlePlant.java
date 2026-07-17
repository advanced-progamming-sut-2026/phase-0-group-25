package src.Model.PlantsAndZombies;

public class BattlePlant extends Plant {
    private double lastActionTime;
    private double timeElapsedAsAlive;
    private PlantStats plantStats;
    private int row;
    private int column;

    private boolean frozen;
    private int iceTime;
    private boolean octopusated;

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


    public PlantStats getPlantStats() {
        return plantStats;
    }


    public double getTimeElapsedAsAlive() {
        return timeElapsedAsAlive;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        if (!this.frozen) {
            this.setIceTime(0);
        }
    }

    public int getIceTime() {
        return iceTime;
    }

    public void setIceTime(int iceTime) {
        this.iceTime = iceTime;
        if (this.iceTime >= 3) {
            this.setFrozen(true);
        }
    }

    public boolean isOctopusated() {
        return octopusated;
    }

    public void setOctopusated(boolean octopusated) {
        this.octopusated = octopusated;
    }
}
