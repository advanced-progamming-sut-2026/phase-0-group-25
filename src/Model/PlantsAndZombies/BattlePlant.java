package src.Model.PlantsAndZombies;

public class BattlePlant extends Plant {
    private double lastActionTime;
    private PlantStats plantStats;


    @Override
    public void update() {

    }

    public Position getPosition() {
        return position;
    }

    public PlantStats getPlantStats() {
        return plantStats;
    }
}
