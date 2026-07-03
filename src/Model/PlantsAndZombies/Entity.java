package src.Model.PlantsAndZombies;

public abstract class Entity {
    protected double currentHP;
    protected boolean isAlive;
    protected Position position;




    public abstract void update();


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

    public Position getPosition() {
        return position;
    }


}
