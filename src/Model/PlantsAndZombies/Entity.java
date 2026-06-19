package src.Model.PlantsAndZombies;

public abstract class Entity {
    protected double currentHP;
    protected boolean isAlive;
    protected Position position;


    public abstract void update();
}
