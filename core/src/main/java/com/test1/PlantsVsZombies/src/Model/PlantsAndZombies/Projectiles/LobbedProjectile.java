package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Audio.SoundManager;
import com.test1.PlantsVsZombies.src.Enums.AudioType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

public class LobbedProjectile extends Projectile {
    private static final double DELTA = 0.1;
    private static final double MIN_FLIGHT_TIME = 1.5;
    private static final double MAX_FLIGHT_TIME = 2.5;
    private static final double GRAVITY = 100.0;

    private final double startX;
    private final double startY;
    private final double targetX;
    private final double targetY;

    private int targetColumn = -1;
    private int targetRow = -1;

    private final double velocityX;
    private double velocityY;

    private double elapsedTime;
    private final double flightTime;

    private final int AoEDamage;
    private final int AoERange;
    private final int damage;

    private final GamePlay GAME = GamePlay.activeInstance;

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
        if (plant.getName().equals(PlantType.WINTER_MELON.getName())) {
            this.icy = true;
        }

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

        SoundManager.getInstance()
            .playSound(AudioType.PROJECTILE_SHOOT);
    }

    public LobbedProjectile(
        BattlePlant plant,
        double startX,
        double startY,
        int targetColumn,
        int targetRow,
        double speed,
        int AoEDamage,
        int AoERange,
        int damage,
        String name) {

        super();

        this.plant = plant;

        this.startX = startX;
        this.startY = startY;

        this.targetColumn = targetColumn;
        this.targetRow = targetRow;

        this.targetX =
            GAME.getRealX(targetColumn);

        this.targetY =
            GAME.getRealY(targetRow);

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

        SoundManager.getInstance()
            .playSound(AudioType.PROJECTILE_SHOOT);
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
        if (targetColumn < 0 ||
            targetRow < 0) {

            return;
        }

        Tile tile =
            GAME.getTileByPosition(
                targetColumn,
                targetRow
            );

        if (tile == null) {
            return;
        }

        boolean hit = false;

        if (!tile.isArable() &&
            tile.getHP() > 0) {

            tile.setHP(
                Math.max(
                    0,
                    tile.getHP() - damage
                )
            );

            hit = true;
        }

        for (Zombie zombie : tile.getZombies()) {
            if (zombie.getCurrentHP() <= 0) {
                continue;
            }

            zombie.takeDamage(
                this,
                damage
            );

            hit = true;
        }

        for (BattlePlant targetPlant : tile.getPlants()) {
            if (targetPlant.equals(plant)) {
                continue;
            }

            if (targetPlant.getCurrentHP() <= 0) {
                continue;
            }

            if (targetPlant.isFrozen()) {
                targetPlant.takeIceDamage(damage);
                hit = true;
            }

            if (targetPlant.isOctopusated()) {
                targetPlant.setOctopusHP(
                    Math.max(
                        0,
                        targetPlant.getOctopusHp() - damage
                    )
                );

                hit = true;
            }
        }

        if (hit) {
            SoundManager.getInstance()
                .playSound(AudioType.PROJECTILE_HIT);
        }
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
