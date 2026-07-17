package Model.PlantsAndZombies;

public class PlantFactory {
    public BattlePlant createBattlePlant(String plantName, int level) {
        PlantStats plantStats = GameDataLoader.getStatsForPlantLevel(plantName, level);

        return new BattlePlant(plantStats, plantName);
    }
}
