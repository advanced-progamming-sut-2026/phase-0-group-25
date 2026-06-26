package src.Model.PlantsAndZombies;

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
}
