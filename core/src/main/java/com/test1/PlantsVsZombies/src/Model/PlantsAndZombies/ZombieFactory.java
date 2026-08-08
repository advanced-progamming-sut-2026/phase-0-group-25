package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

public class ZombieFactory {
    public static Zombie createZombie(String zombieName, Position position) {
        ZombieStats zombieStats = GameDataLoader.getStatsForZombie(zombieName);

        return new Zombie(zombieStats, zombieName, position);
    }

    public static Zombie createZombie(String zombieName) {
        ZombieStats zombieStats = GameDataLoader.getStatsForZombie(zombieName);

        return new Zombie(zombieStats, zombieName);
    }
}
