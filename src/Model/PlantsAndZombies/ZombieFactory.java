package src.Model.PlantsAndZombies;

public class ZombieFactory {
    public static Zombie createZombie(String zombieName, Position position) {
        ZombieStats zombieStats = GameDataLoader.getStatesForZombie(zombieName);

        return new Zombie(zombieStats, zombieName, position);
    }
}
