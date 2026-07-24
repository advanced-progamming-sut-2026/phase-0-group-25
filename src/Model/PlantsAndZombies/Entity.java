package src.Model.PlantsAndZombies;

public abstract class Entity {
    protected String name;
    protected double currentHP;
    protected boolean isAlive = true;
    protected Position position;
    protected int row;
    protected int column;


    public abstract void update();

    public String getName() {
        return name;
    }

    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
        if (this.currentHP <= 0) {
            this.isAlive = false;
        }
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public double getCurrentHP() {
        return currentHP;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public Position getPosition() {
        return position;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }
}
