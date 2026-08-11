package com.test1.PlantsVsZombies.src.Model.Sun;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class Sun {
    private int numberOfSun;
    private Position position;
    private double timeToReach;
    private boolean isCollected = false;
    private boolean isFromSky;

    private final String animationPath = "768/INITIAL/EFFECTS/SUN/SUN.PAM";

    private static double X_DISTANCE = 10;
    private static double Y_DISTANCE = 10;

    public Sun(int numberOfSun, Position position) {
        this.numberOfSun = numberOfSun;
        this.position = new Position(position.getX() + X_DISTANCE,
            position.getY() + Y_DISTANCE);

        this.isFromSky = false;
    }

    public Sun(int numberOfSun, Position position, double timeToReach) {
        this.numberOfSun = numberOfSun;
        this.position = position;
        this.timeToReach = timeToReach;

        this.isFromSky = true;
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
        return animationPath;
    }
}


