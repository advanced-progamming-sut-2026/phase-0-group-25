package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.Random;

public class Zomboss extends Zombie {

    private GamePlay GAME = GamePlay.activeInstance;
    private static int ACTION_INTERVAL = 25;
    private static int STUN_INTERVAL = 10;

    private static int X_RIGHT_LIMIT = 1860;
    private static int Y_UP_LIMIT = 880;
    private static int Y_DOWN_LIMIT = 130;
    private static int X_LEFT_LIMIT = 490;
    private static int TILE_Y_LENGTH = 150;

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
        this.currentSecondRow = this.getRow() + 1;
    }

    @Override
    public void update() {
        System.out.println(GAME.getTotalTimePassed());
        checkLife();

        if (this.isStunned) {
            checkStun();
            return;
        }

        checkStatus();

        if (isTimeForAction()) {
            handleState();
            this.lastActionTime = GAME.getTotalTimePassed();
        }


    }

    private boolean isTimeForAction() {
        return ((GAME.getTotalTimePassed() - this.lastActionTime) >= ACTION_INTERVAL);
    }

    private void spawnZombies() {
        Zombie newZombie1 = makeZombie();
        Zombie newZombie2 = makeZombie();

        GAME.addZombieFromAbility(newZombie1);
        GAME.addZombieFromAbility(newZombie2);
    }

    private void throwProjectile() {
        Random RANDOM = new Random();
        String name = (String) this.zombieStats.getAttributes().get("projectileName");
        int randomColumn = RANDOM.nextInt(7) + 1;
        int randomRow = RANDOM.nextInt(4) + 1;

        Tile targetTile = GAME.getTileByPosition(randomColumn, randomRow);
        Position target = new Position(600, 500);

        double XVelocity = (Double) this.zombieStats.getAttributes().get("X_Velocity");
        double YVelocity = (Double) this.zombieStats.getAttributes().get("Y_Velocity");
        Position launcher = new Position(600, 870);

        Projectile newProjectile = new Projectile(name, launcher,
            XVelocity, YVelocity, target);

        GAME.getProjectiles().add(newProjectile);
    }

    @Override
    public void changeRow() {
        int row = this.getRow();

        if (row == 1) {
            this.position = new Position(
                this.position.getX(),
                this.position.getY() + TILE_Y_LENGTH);
            this.currentSecondRow = 3;
        } else if (row == 4) {
            this.position = new Position(
                this.position.getX(),
                this.position.getY() - TILE_Y_LENGTH);
            this.currentSecondRow = 4;
        } else if ((row > 1) && (row < 4)) {
            Random RANDOM = new Random();
            int randomIndex = RANDOM.nextInt(2);
            int difference = (randomIndex == 1) ? TILE_Y_LENGTH : (-1) * TILE_Y_LENGTH;

            this.position = new Position(
                this.position.getX(),
                this.position.getY() + difference);

            this.currentSecondRow = (randomIndex == 1) ? (this.currentSecondRow + 1) : (this.currentSecondRow - 1);
        }
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
        System.out.println(difference);
        if (difference >= STUN_INTERVAL) {
            this.isStunned = false;
            this.status = "idle";
            this.lastActionTime = GAME.getTotalTimePassed();
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
            this.timeWhenStunned = GAME.getTotalTimePassed();
            this.status = "stun";
        } else if ((HPRatio <= 0.67) && (!this.firstStun)) {
            this.firstStun = true;
            this.isStunned = true;
            this.timeWhenStunned = GAME.getTotalTimePassed();
            this.status = "stun";
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

    public int getCurrentSecondRow() {
        return currentSecondRow;
    }

    private Zombie makeZombie() {
        Random RANDOM = new Random();

        int column = RANDOM.nextInt(5);
        Position zombiePosition = new Position(X_RIGHT_LIMIT - 200, (column * TILE_Y_LENGTH) + Y_DOWN_LIMIT + (TILE_Y_LENGTH / 2));

        int zombieNumber = RANDOM.nextInt(4);
        String zombieName = "";
        switch (zombieNumber) {
            case 0:
                zombieName = "DEFAULT";
                break;
            case 1:
                zombieName = "CONE_HEAD";
                break;
            case 2:
                zombieName = "BUCKET_HEAD";
                break;
            case 3:
                zombieName = "BRICK_HEAD";
                break;
        }

        return ZombieFactory.createZombie(zombieName, zombiePosition);
    }
}

