package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class BigWalnut extends Walnut {

    public BigWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game, float delta) {
        x += speed * delta;

        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) <= 60 && Math.abs(z.getPosition().getY() - y) <= 65) {
                z.takeDamage(2000);
                z.setCurrentHP(0);
            }
        }

        if (x > 1950.0) this.isActive = false;
    }
}
