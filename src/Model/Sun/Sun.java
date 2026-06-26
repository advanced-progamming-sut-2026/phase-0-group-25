package src.Model.Sun;

import src.Model.PlantsAndZombies.Position;

public class Sun {
    private int numberOfSun;
    private Position position;
    private double timeToReach;


    public Sun(int numberOfSun, Position position) {
        this.numberOfSun = numberOfSun;
        this.position = position;
    }

    public Sun(int numberOfSun, Position position, double timeToReach) {
        this.numberOfSun = numberOfSun;
        this.position = position;
        this.timeToReach = timeToReach;
    }

    public int getNumberOfSun() {
        return numberOfSun;
    }
}


