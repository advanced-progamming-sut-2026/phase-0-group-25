package src.Model.Sun;

import src.Model.PlantsAndZombies.Position;

public class Sun {
    private int numberOfSun;
    private Position position;
    private double timeToReach;
    private boolean isCollected = false;
    private boolean isFromSky;

    public Sun(int numberOfSun, Position position) {
        this.numberOfSun = numberOfSun;
        this.position = position;

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

    public void setCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public boolean isFromSky() {
        return isFromSky;
    }

    public void setFromSky(boolean fromSky) {
        isFromSky = fromSky;
    }
}


