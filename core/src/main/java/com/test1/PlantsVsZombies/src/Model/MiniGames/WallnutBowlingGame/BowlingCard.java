package com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame;

public class BowlingCard {
    private final String nutType;
    private float currentY;
    private float targetY;
    private final float speed = 250f;

    public BowlingCard(String nutType, float startY) {
        this.nutType = nutType;
        this.currentY = startY;
        this.targetY = startY;
    }

    public void update(float delta) {
        if (currentY < targetY) {
            currentY = Math.min(targetY, currentY + speed * delta);
        } else if (currentY > targetY) {
            currentY = Math.max(targetY, currentY - speed * delta);
        }
    }

    public String getNutType() { return nutType; }
    public float getCurrentY() { return currentY; }
    public void setTargetY(float targetY) { this.targetY = targetY; }
}
