package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.Random;

public class Zomboss extends Zombie {

    private GamePlay GAME = GamePlay.activeInstance;
    private static int ACTION_INTERVAL = 20;
    private static int STUN_INTERVAL = 10;
    private static int Y_DOWN_LIMIT = 130;
    private static int X_LEFT_LIMIT = 490;

    private int currentSecondRow;

    private boolean isStunned = false;
    private double timeWhenStunned;

    private boolean firstStun = false;
    private boolean secondStun = false;

    private String status = "idle";
    private double statusSpan;

    private double timeWhenTriggered;

    public Zomboss(ZombieStats zombieStats, String name, Position position) {
        super(zombieStats, name, position);

        this.lastActionTime = GAME.getTotalTimePassed();
        this.currentVelocity = 0;
    }

    @Override
    public void update() {
        checkLife();

        checkStatus();

        if (this.isStunned) {
            checkStun();
            return;
        }

        if (isTimeForAction()) {
            handleState();
        }

    }

    private boolean isTimeForAction() {
        return ((GAME.getTotalTimePassed() - this.lastActionTime) >= ACTION_INTERVAL);
    }

    private void spawnZombies() {
        Zombie newZombie1 = ZombieFactory.createZombie("DEFAULT",
            new Position(900, 700));
        Zombie newZombie2 = ZombieFactory.createZombie("BUCKET_HEAD",
            new Position(700, 590));

        GAME.getGameZombies().add(newZombie1);
        GAME.getGameZombies().add(newZombie2);
    }

    private void throwProjectile() {
        Random RANDOM = new Random();
        String name = (String) this.zombieStats.getAttributes().get("projectileName");
        int randomColumn = RANDOM.nextInt(7) + 1;
        int randomRow = RANDOM.nextInt(4) + 1;

        Tile targetTile = GAME.getTileByPosition(randomColumn, randomRow);
        //Position target = findTarget();

        double XVelocity = (Double) this.zombieStats.getAttributes().get("X_Velocity");
        double YVelocity = (Double) this.zombieStats.getAttributes().get("Y_Velocity");
        // Position launcher = findLauncher();

        //Projectile newProjectile = new Projectile(name, laucnher,
        //   XVelocity, YVelocity, target);

        //GAME.getProjectiles().add(newProjectile);
    }

    @Override
    public void changeRow() {
        super.changeRow();
    }

    private void specialAffect() {
        switch (this.name) {
            case "DARK":
                darkSpecialAffect();
                break;
            case "EGYPT":
                egyptAndBeachSpecialAffect();
                break;
            case "ICE":
                iceageSpecialAffect();
                break;
            case "BEACH":
                egyptAndBeachSpecialAffect();
                break;
        }
    }

    @Override
    public String getAnimationPath() {
        return this.zombieStats.getAnimation();
    }

    @Override
    public String getCurrentAnimationName() {
        return this.zombieStats.getStatus().get(this.status);
    }

    private void checkStun() {
        double difference = GAME.getTotalTimePassed() - this.timeWhenStunned;
        if (difference >= STUN_INTERVAL) {
            this.isStunned = false;
        }
    }

    private void handleState() {
        int random = (new Random()).nextInt(4);
        this.timeWhenTriggered = GAME.getTotalTimePassed();
        switch (random) {
            case 0:
                throwProjectile();
                this.status = "throw";
                this.statusSpan = (Double) this.zombieStats.getAttributes().get("throwSpan");
                break;
            case 1:
                spawnZombies();
                this.status = "spawn";
                this.statusSpan = (Double) this.zombieStats.getAttributes().get("spawnSpan");
                break;
            case 2:
                changeRow();
                this.status = "idle";
                break;
            case 3:
                specialAffect();
                this.status = "special";
                this.statusSpan = (Double) this.zombieStats.getAttributes().get("specialSpan");
                break;
        }
    }

    @Override
    public void checkLife() {
        float HPRatio = (float) (this.currentHP / this.zombieStats.getBaseHP());
        if ((HPRatio <= 0.33) && (!this.secondStun)) {
            this.secondStun = true;
            this.isStunned = true;
        } else if ((HPRatio <= 0.67) && (!this.firstStun)) {
            this.firstStun = true;
            this.isStunned = true;
        }
    }

    private void checkStatus() {
        if (this.status.equals("idle")) {
            return;
        }

        double difference = GAME.getTotalTimePassed() - this.timeWhenTriggered;
        if ((difference >= this.statusSpan)) {
            this.status = "idle";
        }

    }

    private void darkSpecialAffect() {
        for (int i = 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, this.getRow());
            for (BattlePlant plant : tile.getPlants()) {
                plant.setAlive(false);
            }

            //tile.setFiring(true);

            tile = GAME.getTileByPosition(i, this.currentSecondRow);
            for (BattlePlant plant : tile.getPlants()) {
                plant.setAlive(false);
            }

            //tile.setFiring(true);
        }
    }

    private void egyptAndBeachSpecialAffect() {
        for (int i = 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, this.getRow());
            for (BattlePlant plant : tile.getPlants()) {
                plant.setAlive(false);
            }
            for (Zombie zombie : tile.getZombies()) {
                if (zombie instanceof Zomboss) {
                    continue;
                }
                zombie.setCurrentHP(0);
            }

            tile = GAME.getTileByPosition(i, this.currentSecondRow);
            for (BattlePlant plant : tile.getPlants()) {
                plant.setAlive(false);
            }
            for (Zombie zombie : tile.getZombies()) {
                if (zombie instanceof Zomboss) {
                    continue;
                }
                zombie.setCurrentHP(0);
            }
        }
    }

    private void iceageSpecialAffect() {
        for (int i = 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, this.getRow());
            for (BattlePlant plant : tile.getPlants()) {
                plant.setIceTime(plant.getIceTime() + 1);
            }

            tile = GAME.getTileByPosition(i, this.currentSecondRow);
            for (BattlePlant plant : tile.getPlants()) {
                plant.setIceTime(plant.getIceTime() + 1);
            }
        }
    }

}

