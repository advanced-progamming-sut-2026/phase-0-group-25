package src.Model.PlantsAndZombies;

public abstract class Entity {
    protected String name;
    protected double currentHP;
    protected boolean isAlive = true;
    protected Position position;


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
        Position rowAndColumn = Position.getRowAndColumn(this.position);

        return (int) rowAndColumn.getY();
    }

    public int getColumn() {
        Position rowAndColumn = Position.getRowAndColumn(this.position);

        return (int) rowAndColumn.getX();
    }


}
