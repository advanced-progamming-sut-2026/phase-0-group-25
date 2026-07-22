package src.Model.MiniGames.IZombieGame;

import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;

public class SunZombie extends Zombie {
    private int ticksSinceLastSun = 0;
    private int baseInterval = 120;

    public SunZombie(Position position) {
        super("SunZombie", position, 1300, 10, 0.1);
    }

    public int generateSun(int totalTicksPassed) {
        ticksSinceLastSun++;

        int currentInterval = Math.max(20, baseInterval - (totalTicksPassed / 40));

        if (ticksSinceLastSun >= currentInterval) {
            ticksSinceLastSun = 0;
            return 25;
        }
        return 0;
    }
}