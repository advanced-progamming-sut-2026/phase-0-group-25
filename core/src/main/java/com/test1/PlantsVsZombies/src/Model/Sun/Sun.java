package com.test1.PlantsVsZombies.src.Model.Sun;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class Sun {
    private final int numberOfSun;
    private Position position;
    private double timeToReach;
    private double targetY;
    private boolean isCollected = false;
    private boolean isFromSky;
    private float fallSpeed = 120f;

    private static double X_DISTANCE = 10;
    private static double Y_DISTANCE = 10;

    public Sun(int numberOfSun, Position position) {
        this.numberOfSun = numberOfSun;
        this.position = new Position(position.getX() + X_DISTANCE,
            position.getY() + Y_DISTANCE);
        this.targetY = position.getY();
        this.isFromSky = false;
    }

    public Sun(int numberOfSun, Position position, double timeToReach) {
        this.numberOfSun = numberOfSun;
        this.position = new Position(position.getX(), 1250);
        this.targetY = position.getY();
        this.isFromSky = true;
    }

    public void update(float delta) {
        if (isFromSky && position.getY() > targetY) {
            double nextY = Math.max(position.getY() - (fallSpeed * delta), targetY);
            position.setY(nextY);
        }
    }

    public int getNumberOfSun() {
        return numberOfSun;
    }

    public Position getPosition() {
        return this.position;
    }

    public double getTimeToReach() {
        return this.timeToReach;
    }

    public void setTimeToReach(double timeToReach) {
        this.timeToReach = timeToReach;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public boolean isFromSky() {
        return isFromSky;
    }

    public void setFromSky(boolean fromSky) {
        isFromSky = fromSky;
    }

    public String getAnimationPath() {
        if (this instanceof RadioActiveSun) {
            return "768/FULL/EFFECTS/SUN_BOMB/SUN_BOMB.PAM";
        } else {
            return "768/INITIAL/EFFECTS/SUN/SUN.PAM";
        }
    }
}


