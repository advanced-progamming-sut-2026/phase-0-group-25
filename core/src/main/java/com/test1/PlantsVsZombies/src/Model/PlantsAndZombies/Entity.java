package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

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

    public double getCurrentHP() {
        return currentHP;
    }

    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
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

    public void setPosition(Position position) {
        this.position = position;
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
