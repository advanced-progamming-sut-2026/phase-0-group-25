package com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame;

public class Brain {
    private final int row;
    private final float x;
    private final float y;
    private boolean eaten = false;

    public Brain(int row, float x, float y) {
        this.row = row;
        this.x = x;
        this.y = y;
    }

    public int getRow() {
        return row;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isEaten() {
        return eaten;
    }

    public void setEaten(boolean eaten) {
        this.eaten = eaten;
    }
}
