package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

public class PlantFactory {
    public static BattlePlant createBattlePlant(String plantName, int level, Position position) {
        PlantStats plantStats = GameDataLoader.getStatsForPlantLevel(plantName, level);
        System.out.println("jokpoijkpo");
        if(plantStats == null){
            System.out.println("NULL");
        };
        return new BattlePlant(plantStats, plantName, position);
    }

    public static BattlePlant createBattlePlant(String plantName, int level) {
        PlantStats plantStats = GameDataLoader.getStatsForPlantLevel(plantName, level);

        return new BattlePlant(plantStats, plantName);
    }
}
