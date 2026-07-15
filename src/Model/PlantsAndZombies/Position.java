package src.Model.PlantsAndZombies;

import java.util.Objects;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public static Position getRowAndColumn(double x, double y) {
        int row = ;//todo
        int column = ;//todo

        return new Position(row, column);
    }

    public static Position getRowAndColumn(Position position) {
        int row = ;//todo
        int column = ;//todo

        return new Position(row, column);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Double.compare(position.x, x) == 0 && Double.compare(position.y, y) == 0;
    }


}
