package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class LobbedProjectile extends Projectile {
    private static int ICE_DAMAGE = 6;
    private double startX;
    private double startY;
    private double targetX;
    private double targetY;
    private double timeToReach;
    private double elapsedTime;
    private int AoEDamage;
    private int AoERange;
    private int damage;
    private boolean isFromLobberPlant;
    private GamePlay GAME = GamePlay.activeInstance;
    private double yVelocity;


    private static double X_VELOCITY = 20;
    private static double Y_VELOCITY = 20;
    private static double GRAVITY = 9.8;

    public LobbedProjectile(BattlePlant plant, double startX, double startY, double targetX, double speed,
                            int AoEDamage, int AoERange, int damage, String name) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = startY;

        this.plant = plant;
        this.name = name;

        this.timeToReach = (targetX - startX) / speed;
        this.elapsedTime = 0;
        this.yVelocity = Y_VELOCITY;

        this.position = new Position(this.startX, this.startY);

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isFromLobberPlant = true;
    }

    public LobbedProjectile(BattlePlant plant, double startX, double startY, double targetX, double targetY, double speed,
                            int AoEDamage, int AoERange, int damage, String name) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;

        this.plant = plant;
        this.name = name;

        this.timeToReach = Math.hypot((startX - targetX), (startY - targetY)) / speed;
        this.elapsedTime = 0;
        this.yVelocity = Y_VELOCITY;

        this.position = new Position(this.startX, this.startY);

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isFromLobberPlant = true;
    }

    @Override
    public void update() {
        this.elapsedTime += 1;
        this.yVelocity -= GRAVITY * 0.02;
        this.position = new Position((position.getX() + (0.1 * X_VELOCITY)),
            (position.getY() + (0.1 * this.yVelocity)));

        /*
        if (elapsedTime >= timeToReach) {
            affectTarget();
            this.isActive = false;
        }
        */
    }

    public void affectTarget() {
        Position targetRowAndColumn = Position.getRowAndColumn(this.targetX, this.startY);
        int targetColumn = (int) targetRowAndColumn.getX();
        int targetRow = (int) targetRowAndColumn.getY();
        ArrayList<Zombie> zombies = findZombiesInRange(targetRow, targetColumn, this.AoERange);

        for (Zombie zombie : zombies) {
            int zombieRow = zombie.getRow();
            int zombieColumn = zombie.getColumn();
            if ((zombieRow == targetRow) || (zombieColumn == targetColumn)) {
                zombie.takeDamage(this, this.damage);
            } else {
                zombie.takeDamage(this, this.damage);
            }
        }


        ArrayList<BattlePlant> plants = findPlantsInRange(targetRow, targetColumn, this.AoERange);

        for (BattlePlant plant : plants) {
            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();
            if ((plantRow == targetRow) || (plantColumn == targetColumn)) {
                if (this.firing) {
                    plant.setFrozen(false);
                }
                if (this.icy) {
                    plant.setIceTime(plant.getIceTime() + 1);
                }
            } else {
                if (this.firing) {
                    if (plant.isFrozen()) {
                        plant.takeIceDamage(ICE_DAMAGE);
                    }
                }
            }
        }
    }

    private ArrayList<Zombie> findZombiesInRange(int targetRow, int targetColumn, int AoERange) {
        ArrayList<Zombie> properZombies = new ArrayList<>();

        for (Zombie zombie : GAME.getGameZombies()) {
            int zombieRow = zombie.getRow();
            int zombieColumn = zombie.getColumn();

            int rowDistance = Math.abs(targetRow - zombieRow);
            int columnDistance = Math.abs(targetColumn - zombieColumn);

            if ((rowDistance <= AoERange) && (columnDistance <= AoERange)) {
                properZombies.add(zombie);
            }
        }

        return properZombies;
    }

    private ArrayList<BattlePlant> findPlantsInRange(int targetRow, int targetColumn, int AoERange) {
        ArrayList<BattlePlant> properPlants = new ArrayList<>();

        for (BattlePlant plant : GAME.getPlants()) {
            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();

            int rowDistance = Math.abs(targetRow - plantRow);
            int columnDistance = Math.abs(targetColumn - plantColumn);

            if ((rowDistance <= AoERange) && (columnDistance <= AoERange)) {
                properPlants.add(plant);
            }
        }

        return properPlants;
    }

    public boolean isFromLobberPlant() {
        return isFromLobberPlant;
    }

}
