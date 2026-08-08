package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class ExplodingWalnut extends Walnut {

    public ExplodingWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game) {
        x += speed;

        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) < 5 && Math.abs(z.getPosition().getY() - y) < 5) {
                Explosion(game);
                this.isActive = false;
                return;
            }
        }

        if (x > 1800.0) this.isActive = false;
    }

    public void Explosion(WalnutBowling game) {
        System.out.println("BOOM! Explode O' Nut triggered!");
        for (Zombie z : game.getGameZombies()) {
            if (Math.abs(z.getPosition().getX() - x) <= 350 && Math.abs(z.getPosition().getY() - y) <= 350) {
                z.takeDamage(1800);
            }
        }
    }
}
