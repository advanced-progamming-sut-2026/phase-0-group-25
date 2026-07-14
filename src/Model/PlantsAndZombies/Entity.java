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

    public Position getPosition() {
        return position;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public double getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
    }
}
