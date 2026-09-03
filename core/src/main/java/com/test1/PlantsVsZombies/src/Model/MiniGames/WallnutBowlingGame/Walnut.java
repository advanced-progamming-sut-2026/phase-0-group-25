package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

public abstract class Walnut {
    protected double x;
    protected double y;
    protected boolean isActive;
    protected double speed = 450.0;

    public Walnut(double x, double y) {
        this.x = x;
        this.y = y;
        this.isActive = true;
    }

    public abstract void update(WalnutBowling game, float delta);

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
