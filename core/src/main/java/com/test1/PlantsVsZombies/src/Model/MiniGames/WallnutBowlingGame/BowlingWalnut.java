package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class BowlingWalnut extends Walnut {
    private final int NORMAL_DAMAGE = 400;
    private double velocityX = 1.0;
    private double velocityY = 0.0;
    private int hitCount = 0;


    private static final float MIN_LAWN_Y = 205f;
    private static final float MAX_LAWN_Y = 805f;

    public BowlingWalnut(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(WalnutBowling game, float delta) {
        x += velocityX * speed * delta;
        y += velocityY * speed * delta;


        if (y <= MIN_LAWN_Y && velocityY < 0) {
            y = MIN_LAWN_Y;
            velocityY = -velocityY;
        }

        else if (y >= MAX_LAWN_Y && velocityY > 0) {
            y = MAX_LAWN_Y;
            velocityY = -velocityY;
        }


        for (Zombie z : game.getGameZombies()) {
            if (z.isAlive() && Math.abs(z.getPosition().getX() - x) <= 60 && Math.abs(z.getPosition().getY() - y) <= 65) {
                z.takeDamage(NORMAL_DAMAGE);
                hitCount++;
                changeDirection(true);
                break;
            }
        }


        if (x > 1950.0) {
            this.isActive = false;
        }
    }

    public void changeDirection(boolean hitZombie) {
        if (hitZombie) {
            if (hitCount == 1) {

                velocityY = (Math.random() > 0.5) ? 1.0 : -1.0;
                velocityX = 1.0;
            } else {

                velocityY = -velocityY;
            }
        } else {
            velocityY = -velocityY;
        }
    }
}
