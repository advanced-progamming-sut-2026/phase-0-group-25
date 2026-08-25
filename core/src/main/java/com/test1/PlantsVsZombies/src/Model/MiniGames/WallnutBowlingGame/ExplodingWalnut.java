package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class ExplodingWalnut extends Walnut {

    public ExplodingWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game, float delta) {
        x += speed * delta;

        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) <= 60 && Math.abs(z.getPosition().getY() - y) <= 65) {
                explosion(game);
                this.isActive = false;
                return;
            }
        }

        if (x > 1950.0) this.isActive = false;
    }

    public void explosion(WalnutBowling game) {
        game.addExplosionEffect((float) x, (float) y);
        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.hypot(z.getPosition().getX() - x, z.getPosition().getY() - y) <= 230) {
                z.takeDamage(1800);
                z.setCurrentHP(0);
            }
        }
    }
}
