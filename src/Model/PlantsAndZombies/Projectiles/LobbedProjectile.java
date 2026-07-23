package src.Model.PlantsAndZombies.Projectiles;

import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;
import java.util.Map;

public class LobbedProjectile extends Projectile {
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

    public LobbedProjectile(double startX, double startY, double targetX, double speed,
                            int AoEDamage, int AoERange, int damage) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = startY;

        this.timeToReach = (targetX - startX) / speed;
        this.elapsedTime = 0;

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;
        this.isActive = true;
        this.icy = false;
        this.isFromLobberPlant = true;
    }

    public LobbedProjectile(double startX, double startY, double targetX, double targetY, double speed,
                            int AoEDamage, int AoERange, int damage) {
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;

        this.timeToReach = Math.hypot((startX - targetX), (startY - targetY)) / speed;
        this.elapsedTime = 0;

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;
        this.isActive = true;
        this.icy = false;
        this.isFromLobberPlant = true;
    }

    @Override
    public void update() {
        this.elapsedTime += 1;
        if (elapsedTime >= timeToReach) {
            affectTarget();
            this.isActive = false;
        }
    }

    public void affectTarget() {
        Position targetRowAndColumn = Position.getRowAndColumn(this.targetX, this.startY);
        int targetColumn = (int) targetRowAndColumn.getX();
        int targetRow = (int) targetRowAndColumn.getY();
        ArrayList<Zombie> zombies = findZombiesInRange(targetRow, targetColumn, this.AoERange);

        for (Zombie zombie : zombies) {
            Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition().getX(), zombie.getPosition().getY());
            int zombieRow = (int) zombieRowAndColumn.getX();
            int zombieColumn = (int) zombieRowAndColumn.getY();
            if ((zombieRow == targetRow) || (zombieColumn == targetColumn)) {
                //reduce hp of zombie according to directDamage//todo
            } else {
                //reduce hp of zombie according to AoEDamage//todo
            }
        }
    }

    private ArrayList<Zombie> findZombiesInRange(int targetRow, int targetColumn, int AoERange) {
        //todo
        ArrayList<Zombie> properZombies = new ArrayList<>();

        for (Zombie zombie : game.getAliveZombies()) {
            Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition().getX(), zombie.getPosition().getY());
            int zombieRow = (int) zombieRowAndColumn.getX();
            int zombieColumn = (int) zombieRowAndColumn.getY();

            int rowDistance = Math.abs(targetRow - zombieRow);
            int columnDistance = Math.abs(targetColumn - zombieColumn);

            if ((rowDistance <= AoERange) && (columnDistance <= AoERange)) {
                properZombies.add(zombie);
            }
        }

        return properZombies;
    }

    public boolean isFromLobberPlant() {
        return isFromLobberPlant;
    }

}
