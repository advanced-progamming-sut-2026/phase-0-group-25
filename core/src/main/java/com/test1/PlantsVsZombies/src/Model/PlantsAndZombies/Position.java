package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Position getRowAndColumn(double x, double y) {
        int column = (int) Math.floor((x - 490) / 152.2) + 1;
        int row = (int) Math.floor((y - 130) / 150) + 1;

        return new Position(column, row);
    }

    public static Position getRowAndColumn(Position position) {
        Position newPosition = getRowAndColumn(position.getX(), position.getY());
        return newPosition;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return (Math.abs(this.x - position.getX()) <= 20) &&
            (Math.abs(this.y - position.getY()) <= 20);
    }

    public double distance(Position position) {
        double distanceX = this.x - position.getX();
        double distanceY = this.y - position.getY();

        return (Math.hypot(distanceX, distanceY));
    }
}
