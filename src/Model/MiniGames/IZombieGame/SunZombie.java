package src.Model.MiniGames.IZombieGame;

import src.Model.PlantsAndZombies.GameDataLoader;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.PlantsAndZombies.Armors.ArmorConfig;

public class SunZombie extends Zombie {
    private int ticksSinceLastSun = 0;
    private int baseInterval = 120;

    public SunZombie(Position position) {
        super(GameDataLoader.getStatsForZombie("DEFAULT"), "SunZombie", position);

        this.name = "SunZombie";
        this.currentHP = 190;

        this.getActiveArmors().add(ArmorConfig.BUCKET.createArmor());

        this.setCurrentVelocity(0.185);
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

    @Override
    public void update() {
        super.update();
    }
}