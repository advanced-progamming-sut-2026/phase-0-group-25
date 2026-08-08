package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class BigWalnut extends Walnut {

    public BigWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game) {
        x += speed;

        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) < 5 && Math.abs(z.getPosition().getY() - y) < 5) {
                z.takeDamage(1800);
                System.out.println("CRUNCH! Big Walnut crushed a zombie!");
            }
        }

        if (x > 1800.0) this.isActive = false;
    }
}
