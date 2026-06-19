package src.Model.PlantsAndZombies;

public abstract class Entity {
    protected double health;
    protected boolean isAlive;
    protected Position position;

    public abstract void update();

    public boolean isAlive() {
        return this.isAlive;
    }
}
