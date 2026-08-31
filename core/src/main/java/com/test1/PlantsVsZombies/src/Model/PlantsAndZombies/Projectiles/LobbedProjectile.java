package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class LobbedProjectile extends Projectile {
    private static final double DELTA = 0.1;
    private static final double MIN_FLIGHT_TIME = 1.5;
    private static final double MAX_FLIGHT_TIME = 2.5;
    private static final double GRAVITY = 100.0;

    private double startX;
    private double startY;
    private double targetX;
    private double targetY;

    private double velocityX;
    private double velocityY;

    private double elapsedTime;
    private double flightTime;

    private int AoEDamage;
    private int AoERange;
    private int damage;

    private GamePlay GAME = GamePlay.activeInstance;

    public LobbedProjectile(
        BattlePlant plant,
        double startX,
        double startY,
        double targetX,
        double targetY,
        double speed,
        int AoEDamage,
        int AoERange,
        int damage,
        String name) {

        super();

        this.plant = plant;

        this.startX = startX;
        this.startY = startY;

        this.targetX = targetX;
        this.targetY = targetY;

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;

        this.name = name;

        this.position =
            new Position(startX, startY);

        this.isActive = true;

        double distance =
            Math.abs(targetX - startX);

        this.flightTime =
            calculateFlightTime(distance);

        this.elapsedTime = 0;

        this.velocityX =
            (targetX - startX)
                / flightTime;

        this.velocityY =
            (targetY - startY
                + 0.5 * GRAVITY
                * flightTime
                * flightTime)
                / flightTime;
    }

    private double calculateFlightTime(double distance) {
        double minDistance = 100.0;
        double maxDistance = 800.0;

        double normalized =
            (distance - minDistance)
                / (maxDistance - minDistance);

        if (normalized < 0) {
            normalized = 0;
        }

        if (normalized > 1) {
            normalized = 1;
        }

        return MIN_FLIGHT_TIME
            + normalized
            * (MAX_FLIGHT_TIME - MIN_FLIGHT_TIME);
    }

    @Override
    public void update() {
        if (!isActive) {
            return;
        }

        elapsedTime += DELTA;

        double newX =
            position.getX()
                + velocityX * DELTA;

        double newY =
            position.getY()
                + velocityY * DELTA;

        position =
            new Position(
                newX,
                newY
            );

        velocityY -= GRAVITY * DELTA;

        if (elapsedTime >= flightTime) {
            position =
                new Position(
                    targetX,
                    targetY
                );

            affectTarget();

            isActive = false;
        }
    }

    private void affectTarget() {
        for (Zombie zombie : GAME.getGameZombies()) {
            if (zombie.getCurrentHP() <= 0) {
                continue;
            }

            if (zombie.getRow() != plant.getRow()) {
                continue;
            }

            double distance =
                Math.abs(
                    zombie.getPosition().getX()
                        - targetX
                );

            if (distance <= 50) {
                zombie.takeDamage(
                    this,
                    damage
                );
            }
        }

        int row = plant.getRow();
        int column = findTargetColumn();

        if (column == -1) {
            return;
        }

        com.test1.PlantsVsZombies.src.Model.Tile tile =
            GAME.getTileByPosition(
                column,
                row
            );

        if (tile != null
            && !tile.isArable()
            && tile.getHP() > 0) {

            tile.setHP(
                Math.max(
                    0,
                    tile.getHP() - damage
                )
            );
        }
    }

    private int findTargetColumn() {
        int row = plant.getRow();

        for (int column = plant.getColumn();
             column <= 9;
             column++) {

            double tileX =
                GAME.getRealX(column);

            double tileY =
                GAME.getRealY(row);

            if (Math.abs(tileX - targetX) <= 1
                && Math.abs(tileY - targetY) <= 1) {
                return column;
            }
        }

        return -1;
    }

    public double getTargetX() {
        return targetX;
    }

    public double getTargetY() {
        return targetY;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }
}
