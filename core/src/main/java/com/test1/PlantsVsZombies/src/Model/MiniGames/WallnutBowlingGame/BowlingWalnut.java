package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class BowlingWalnut extends Walnut {
    private final int NORMAL_DAMAGE = 200;
    private double velocityX = 1.0;
    private double velocityY = 0.0;
    private int hitCount = 0;

    public BowlingWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game) {
        x += velocityX * speed;
        y += velocityY * speed;

        if ((y <= 40 && velocityY < 0) || (y >= 1040 && velocityY > 0)) {
            changeDirection(false);
        }

        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) < 5 && Math.abs(z.getPosition().getY() - y) < 5) {
                System.out.println("DAMAGE! Bowling Walnut crushed a zombie!");
                z.takeDamage(NORMAL_DAMAGE);
                hitCount++;
                changeDirection(true);
                break;
            }
        }

        if (x > 1800.0) {
            this.isActive = false;
        }
    }

    public void changeDirection(boolean hitZombie) {
        if (hitZombie) {
            if (hitCount == 1) {
                velocityY = (Math.random() > 0.5) ? 1.0 : -1.0;
            } else {
                velocityY = -velocityY;
            }
        } else {
            velocityY = -velocityY;
        }
    }
}
