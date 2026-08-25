package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;

import java.util.ArrayList;

public class LobbedProjectile extends Projectile {

    private static final int ICE_DAMAGE = 6;

    /*
     * Projectile physics
     *
     * The projectile always starts and ends at the same Y
     * for the normal Lobber constructor.
     */
    private static final double DELTA = 0.1;

    private static final double MIN_FLIGHT_TIME = 1.5;
    private static final double MAX_FLIGHT_TIME = 2.5;

    /*
     * This is game-coordinate gravity, not real-world gravity.
     * Increase it for a sharper arc.
     * Decrease it for a higher/wider arc.
     */
    private static final double GRAVITY = 100.0;

    private double startX;
    private double startY;

    private double targetX;
    private double targetY;

    private double timeToReach;
    private double elapsedTime;

    private double velocityX;
    private double velocityY;

    private int AoEDamage;
    private int AoERange;
    private int damage;

    private boolean isFromLobberPlant;

    private GamePlay GAME = GamePlay.activeInstance;


    /*
     * Constructor for the normal Lobber projectile.
     *
     * targetY is automatically equal to startY.
     */
    public LobbedProjectile(
        BattlePlant plant,
        double startX,
        double startY,
        double targetX,
        double speed,
        int AoEDamage,
        int AoERange,
        int damage,
        String name) {

        this.startX = startX;
        this.startY = startY;

        this.targetX = targetX;
        this.targetY = startY;

        this.plant = plant;
        this.name = name;

        this.elapsedTime = 0;

        /*
         * Calculate the horizontal distance.
         */
        double distance = Math.abs(targetX - startX);

        /*
         * Calculate how long the projectile should stay
         * in the air.
         *
         * Close targets  -> around 1.5 sec
         * Far targets    -> around 2.5 sec
         */
        this.timeToReach = calculateFlightTime(distance);

        /*
         * Calculate horizontal velocity.
         *
         * We want:
         *
         * startX + velocityX * timeToReach = targetX
         */
        this.velocityX =
            (targetX - startX) / timeToReach;

        /*
         * Since startY == targetY, the projectile must
         * come back to the same height.
         *
         * y = y0 + vy*t - 0.5*g*t²
         *
         * At t = timeToReach:
         *
         * 0 = vy*T - 0.5*g*T²
         *
         * Therefore:
         *
         * vy = g*T/2
         */
        this.velocityY =
            (GRAVITY * timeToReach) / 2.0;

        this.position = new Position(
            this.startX,
            this.startY
        );

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;

        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;

        this.isFromLobberPlant = true;
    }


    /*
     * Constructor where targetY can be different from startY.
     *
     * This version also calculates a proper parabolic trajectory.
     */
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

        this.startX = startX;
        this.startY = startY;

        this.targetX = targetX;
        this.targetY = targetY;

        this.plant = plant;
        this.name = name;

        this.elapsedTime = 0;

        /*
         * Calculatedistance between start and target.
         */
        double distance = Math.hypot(
            targetX - startX,
            targetY - startY
        );

        this.timeToReach = calculateFlightTime(distance);

        /*
         * Horizontal velocity.
         */
        this.velocityX =
            (targetX - startX) / timeToReach;

        /*
         * Calculate vertical velocity so that
         * the projectile reaches targetY exactly
         * at timeToReach.
         *
         * targetY =
         * startY + vy*T - 0.5*g*T²
         *
         * Therefore:
         *
         * vy =
         * (targetY - startY + 0.5*g*T²) / T
         */
        this.velocityY =
            (targetY - startY
                + 0.5 * GRAVITY * timeToReach * timeToReach)
                / timeToReach;

        this.position = new Position(
            this.startX,
            this.startY
        );

        this.AoEDamage = AoEDamage;
        this.AoERange = AoERange;
        this.damage = damage;

        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;

        this.isFromLobberPlant = true;
    }


    /*
     * Determines flight time from distance.
     *
     * Short distance:
     *      1.5 seconds
     *
     * Long distance:
     *      2.5 seconds
     *
     * Anything between them is interpolated.
     */
    private double calculateFlightTime(double distance) {

        /*
         * These values should roughly represent
         * the minimum and maximum projectile distances
         * on your game board.
         *
         * Adjust them according to your board coordinates.
         */
        double minDistance = 100.0;
        double maxDistance = 800.0;

        /*
         * Convert distance to [0, 1].
         */
        double normalized =
            (distance - minDistance)
                / (maxDistance - minDistance);

        /*
         * Clamp to [0, 1].
         */
        normalized =
            Math.max(0.0, Math.min(1.0, normalized));

        /*
         * Interpolate between 1.5 and 2.5 seconds.
         */
        return MIN_FLIGHT_TIME
            + normalized
            * (MAX_FLIGHT_TIME - MIN_FLIGHT_TIME);
    }


    @Override
    public void update() {

        /*
         * Your game updates every 0.1 seconds.
         */
        elapsedTime += DELTA;

        /*
         * Update position using current velocity.
         */
        position = new Position(
            position.getX()
                + velocityX * DELTA,

            position.getY()
                + velocityY * DELTA
        );

        /*
         * Gravity changes vertical velocity.
         */
        velocityY -= GRAVITY * DELTA;


        /*
         * Projectile has reached its destination.
         */
        if (elapsedTime >= timeToReach) {

            /*
             * Force the projectile to exactly the target.
             *
             * This prevents small floating-point errors
             * from leaving it a few pixels away.
             */
            position = new Position(
                targetX,
                targetY
            );

            affectTarget();

            this.isActive = false;
        }
    }


    public void affectTarget() {

        Position targetRowAndColumn =
            Position.getRowAndColumn(
                this.targetX,
                this.targetY
            );

        int targetColumn =
            (int) targetRowAndColumn.getX();

        int targetRow =
            (int) targetRowAndColumn.getY();

        ArrayList<Zombie> zombies =
            findZombiesInRange(
                targetRow,
                targetColumn,
                this.AoERange
            );

        for (Zombie zombie : zombies) {

            int zombieRow = zombie.getRow();
            int zombieColumn = zombie.getColumn();

            if ((zombieRow == targetRow)
                && (zombieColumn == targetColumn)) {

                zombie.takeDamage(
                    this,
                    this.damage
                );
                if (this.name.equals("butter")) {
                    zombie.setButtered(true);
                }

            } else {

                zombie.takeDamage(
                    this,
                    this.damage
                );
            }
        }


        ArrayList<BattlePlant> plants =
            findPlantsInRange(
                targetRow,
                targetColumn,
                this.AoERange
            );

        for (BattlePlant plant : plants) {

            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();

            if ((plantRow == targetRow)
                || (plantColumn == targetColumn)) {

                if (this.firing) {
                    plant.setFrozen(false);
                }

                if (this.icy) {
                    plant.setIceTime(
                        plant.getIceTime() + 1
                    );
                }

            } else {

                if (this.firing) {

                    if (plant.isFrozen()) {

                        plant.takeIceDamage(
                            ICE_DAMAGE
                        );
                    }
                }
            }
        }
    }


    private ArrayList<Zombie> findZombiesInRange(
        int targetRow,
        int targetColumn,
        int AoERange) {

        ArrayList<Zombie> properZombies =
            new ArrayList<>();

        for (Zombie zombie : GAME.getGameZombies()) {

            int zombieRow = zombie.getRow();
            int zombieColumn = zombie.getColumn();

            int rowDistance =
                Math.abs(targetRow - zombieRow);

            int columnDistance =
                Math.abs(targetColumn - zombieColumn);

            if ((rowDistance <= AoERange)
                && (columnDistance <= AoERange)) {

                properZombies.add(zombie);
            }
        }

        return properZombies;
    }


    private ArrayList<BattlePlant> findPlantsInRange(
        int targetRow,
        int targetColumn,
        int AoERange) {

        ArrayList<BattlePlant> properPlants =
            new ArrayList<>();

        for (BattlePlant plant : GAME.getPlants()) {

            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();

            int rowDistance =
                Math.abs(targetRow - plantRow);

            int columnDistance =
                Math.abs(targetColumn - plantColumn);

            if ((rowDistance <= AoERange)
                && (columnDistance <= AoERange)) {

                properPlants.add(plant);
            }
        }

        return properPlants;
    }


    public boolean isFromLobberPlant() {
        return isFromLobberPlant;
    }
}
