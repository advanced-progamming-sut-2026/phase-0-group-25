package src.Model.MiniGames.IZombieGame;

import src.Model.PlantsAndZombies.GameDataLoader;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlantsAndZombies.Armors.ArmorConfig;

public class SunZombie extends Zombie {
    private int ticksSinceLastSun = 0;
    private int baseInterval = 300;

    public SunZombie(Position position) {
        super(GameDataLoader.getStatsForZombie("DEFAULT"), "SunZombie", position);

        this.getZombieStats().setName("SunZombie");
        this.name = "SunZombie";
        this.currentHP = 1290;
        this.setCurrentVelocity(0.1);
    }

    public int generateSun(int totalTicksPassed) {
        ticksSinceLastSun++;

        int currentInterval = Math.max(150, baseInterval - (totalTicksPassed / 20));

        if (ticksSinceLastSun >= currentInterval) {
            ticksSinceLastSun = 0;
            return 25;
        }
        return 0;
    }

    @Override
    public void update() {
        super.update();
    }
}