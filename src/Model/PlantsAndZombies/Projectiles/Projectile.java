package src.Model.PlantsAndZombies.Projectiles;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;

public class Projectile {
    private double velocityX;
    private double velocityY;

    private int damage;

    private Position position;
    private int pierceAmount;
    private int range;

    private int baseRow;
    private int baseColumn;
    private int currentRow;
    private int currentColumn;

    protected boolean isActive;
    protected boolean icy;
    private boolean poisonous;
    private boolean isHypnotizer;

    public Projectile() {

    }
    public Projectile(double velocityX, double velocityY, BattlePlant plant,
                      int damage, int pierceAmount) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.position = plant.getPosition();
        this.baseColumn = plant.getColumn();
        this.baseRow = plant.getRow();
        this.currentColumn = baseColumn;
        this.currentRow = baseRow;

        this.damage = damage;

        this.pierceAmount = pierceAmount;
        this.range = 11;
        this.isActive = true;
        this.icy = false;
        this.poisonous = false;
    }

    public Projectile(double velocityX, double velocityY, BattlePlant plant,
                      int damage, int pierceAmount, int range) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.position = plant.getPosition();
        this.baseColumn = plant.getColumn();
        this.baseRow = plant.getRow();
        this.currentColumn = baseColumn;
        this.currentRow = baseRow;

        this.damage = damage;

        this.pierceAmount = pierceAmount;
        this.range = range;
        this.isActive = true;
        this.icy = false;
        this.poisonous = false;
    }


    public int getBaseRow() {
        return baseRow;
    }

    public int getBaseColumn() {
        return baseColumn;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentColumn() {
        return currentColumn;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }

    public void setHypnotizer(boolean isHypnotizer) {
        this.isHypnotizer = isHypnotizer;
    }

    public void update() {
        position.setX(position.getX() + (0.1 * velocityX));
        position.setY(position.getY() + (0.1 * velocityY));

        //todo: updating the mechanism of row and column

        updateActivation();

        checkCollision();
    }

    public void updateActivation() {
        if (((this.currentRow - this.baseRow) > this.range) ||
                ((this.currentColumn - this.baseColumn) > this.range)) {
            this.isActive = false;
        }
    }

    public void checkCollision() {

        for (Zombie zombie : game.getAliveZombies()) {
            if (position.equals(zombie.getPosition())) {
                //todo: reduce hp of zombie or its armor and the pierce amount of projectile
            }
        }
    }

    public void setIcy(boolean icy) {
        this.icy = icy;
    }

    public void setPoisonous(boolean poisonous) {
        this.poisonous = poisonous;
    }

}
