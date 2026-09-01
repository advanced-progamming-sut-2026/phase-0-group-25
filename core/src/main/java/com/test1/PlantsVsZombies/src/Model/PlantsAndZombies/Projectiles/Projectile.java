package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Audio.SoundManager;
import com.test1.PlantsVsZombies.src.Enums.AudioType;
import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.Random;

public class Projectile {
    private static int X_RIGHT_LIMIT = 1860;
    private static int Y_UP_LIMIT = 880;
    private static int Y_DOWN_LIMIT = 130;
    private static int X_LEFT_LIMIT = 490;
    private static double VELOCITY_MULTIPLIER = 4;

    protected boolean isActive = true;
    protected boolean icy;
    protected boolean firing;
    protected boolean poisonous;
    protected BattlePlant plant;
    private double velocityX;
    private double velocityY;
    private int damage;
    protected Position position;
    private Position basePosition;
    private int pierceAmount;
    private int range;
    private int knockback;
    private boolean isHypnotizer;
    protected String name;
    private boolean blueFiring = false;
    private boolean isForZomboss = false;

    protected Position offset;
    private Position target;

    private GamePlay GAME = GamePlay.activeInstance;

    public Projectile() {
        SoundManager.getInstance().playSound(AudioType.PROJECTILE_SHOOT);
    }

    public Projectile(BattlePlant plant, double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount, double offsetX, double offsetY) {
        this.plant = plant;
        this.velocityX = velocityX * VELOCITY_MULTIPLIER;
        this.velocityY = velocityY * VELOCITY_MULTIPLIER;
        this.position = new Position(position.getX() + offsetX, position.getY());
        this.basePosition = new Position(position.getX() + offsetX, position.getY());
        this.name = (String) plant.getPlantStats().getAttributes().get("projectileName");


        this.damage = damage;
        this.knockback = 0;

        this.offset = new Position(offsetX, offsetY);

        this.pierceAmount = pierceAmount;
        this.range = 11;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isHypnotizer = false;
        SoundManager.getInstance().playSound(AudioType.PROJECTILE_SHOOT);
    }

    public Projectile(BattlePlant plant, double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount, int range, double offsetX, double offsetY) {
        this.plant = plant;
        this.velocityX = velocityX * VELOCITY_MULTIPLIER;
        this.velocityY = velocityY * VELOCITY_MULTIPLIER;
        this.position = new Position(position.getX() + offsetX, position.getY());
        this.basePosition = new Position(position.getX() + offsetX, position.getY());

        this.name = (String) plant.getPlantStats().getAttributes().get("projectileName");


        this.damage = damage;
        this.knockback = 0;

        this.offset = new Position(offsetX, offsetY);

        this.pierceAmount = pierceAmount;
        this.range = range;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isHypnotizer = false;
        SoundManager.getInstance().playSound(AudioType.PROJECTILE_SHOOT);
    }

    public Projectile(BattlePlant plant, double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount, int range, String name,
                      double offsetX, double offsetY) {
        this.plant = plant;
        this.velocityX = velocityX * VELOCITY_MULTIPLIER;
        this.velocityY = velocityY * VELOCITY_MULTIPLIER;
        this.position = new Position(position.getX() + offsetX, position.getY());
        this.basePosition = new Position(position.getX() + offsetX, position.getY());
        this.name = name;


        this.damage = damage;
        this.knockback = 0;

        this.offset = new Position(offsetX, offsetY);

        this.pierceAmount = pierceAmount;
        this.range = range;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isHypnotizer = false;
        SoundManager.getInstance().playSound(AudioType.PROJECTILE_SHOOT);
    }

    public Projectile(String name, Position launcher,
                      double velocityX, double velocityY, Position target) {
        this.isForZomboss = true;
        this.offset = new Position(0, 0);
        this.name = name;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.target = target;

        this.position = launcher;
    }


    public void setHypnotizer(boolean isHypnotizer) {
        this.isHypnotizer = isHypnotizer;
    }

    public void update() {
        this.position = new Position((position.getX() + (0.1 * velocityX)),
            (position.getY() + (0.1 * velocityY)));

        if (this.isForZomboss) {
            checkZombossCollision();
            return;
        }


        updateActivation();

        checkTombCollision();

        checkCollision();
    }

    public void updateActivation() {
        Position currentRowAndColumn = Position.getRowAndColumn(this.position);
        int currentRow = (int) currentRowAndColumn.getY();
        int currentColumn = (int) currentRowAndColumn.getX();

        Position baseRowAndColumn = Position.getRowAndColumn(this.basePosition);
        int baseRow = (int) baseRowAndColumn.getY();
        int baseColumn = (int) baseRowAndColumn.getX();

        if (((currentRow - baseRow) > this.range) ||
            ((currentColumn - baseColumn) > this.range)) {
            this.isActive = false;
        }

        if (this.position.getX() >= X_RIGHT_LIMIT ||
            this.position.getX() <= X_LEFT_LIMIT) {
            this.isActive = false;
        }

        if (this.position.getY() >= Y_UP_LIMIT ||
            this.position.getY() <= Y_DOWN_LIMIT) {
            this.isActive = false;
        }
    }

    private void checkTombCollision() {
        Position rowAndColumn = Position.getRowAndColumn(this.position.getX(), this.position.getY());
        int column = (int) rowAndColumn.getX();
        int row = (int) rowAndColumn.getY();
        Tile tile = GAME.getTileByPosition(column, row);
        if (tile == null) {
            return;
        }

        if (GAME.getChapterType() != null) {
            if (GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
                GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
                GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {

                if (!tile.isArable() && tile.getHP() > 0) {
                    tile.setHP(Math.max(0, tile.getHP() - this.damage));

                    if (tile.getHP() == 0) {
                        tile.setArable(true);

                        if (GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {
                            Position spawnPos = new Position(GAME.getRealX(column), GAME.getRealY(row));
                            Zombie zombie = ZombieFactory.createZombie("DEFAULT", spawnPos);
                            zombie.setRow(row);
                            zombie.setColumn(column);
                            GAME.getGameZombies().add(zombie);
                            System.out.println("Ice block broken! Zombie spawned at (" + column + ", " + row + ")!");
                        } else if (GAME.getChapterType().equals(ChapterType.DARK_AGE)) {
                            Position dropPos = new Position(GAME.getRealX(column), GAME.getRealY(row));
                            if (tile.getGraveType() == Tile.GraveType.PLANT_FOOD) {
                                GAME.glowingAward(dropPos);
                            } else if (tile.getGraveType() == Tile.GraveType.SUN) {
                                GAME.getActiveSuns().add(new Sun(50, dropPos, 0));
                            }
                            System.out.println("Dark Age tomb destroyed!");
                        } else {
                            System.out.println("Tomb destroyed!!!!!");
                        }
                    }
                    this.isActive = false;
                    SoundManager.getInstance().playSound(AudioType.PROJECTILE_HIT);
                }
            }
        }
    }

    public void checkCollision() {
        for (Zombie zombie : GAME.getGameZombies()) {
            if (!zombie.isHypnotized() &&
                zombie.getCurrentHP() > 0) {
                if (this.position.equals(zombie.getPosition())) {
                    if ((zombie.getZombieStats().getName().equals("SNORKEL")) &&
                        (zombie.isSubmarine())) {
                        continue;
                    }

                    if (zombie.getName().equals("IMP_DRAGON")) {
                        if (this.isFiring()) {
                            continue;
                        }
                    }

                    zombie.takeDamage(this, this.damage);
                    SoundManager.getInstance().playSound(AudioType.PROJECTILE_HIT);
                    zombie.setPosition(new Position(
                        zombie.getPosition().getX() + this.knockback,
                        zombie.getPosition().getY()));

                    this.setPierceAmount(this.getPierceAmount() - 1);
                }
            }
        }

        for (BattlePlant plant : GAME.getGamePlants()) {
            if (plant.equals(this.plant)) {
                continue;
            }
            if (plant.getPosition().equals(this.position)) {
                if (plant.isOctopusated()) {
                    plant.setOctopusHP(plant.getOctopusHp() - this.damage);
                    this.setPierceAmount(this.getPierceAmount() - 1);
                    SoundManager.getInstance().playSound(AudioType.PROJECTILE_HIT);
                }
                if (plant.isFrozen()) {
                    plant.takeIceDamage(this.damage);
                    this.setPierceAmount(this.getPierceAmount() - 1);
                    SoundManager.getInstance().playSound(AudioType.PROJECTILE_HIT);
                }
            }
        }

    }

    public void setKnockback(int knockback) {
        this.knockback = knockback;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isIcy() {
        return icy;
    }

    public void setIcy(boolean icy) {
        this.icy = icy;
    }


    public boolean isFiring() {
        return firing;
    }

    public void setFiring(boolean firing) {
        this.firing = firing;
        if (this.firing) {
            if (this.damage == 20) {
                this.damage = 40;
            }
            this.icy = false;
        }

    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public void setPoisonous(boolean poisonous) {
        this.poisonous = poisonous;
    }

    public int getPierceAmount() {
        return pierceAmount;
    }

    public void setPierceAmount(int pierceAmount) {
        this.pierceAmount = pierceAmount;
        if (this.pierceAmount <= 0) {
            this.isActive = false;
        }
    }

    public Position getPosition() {
        return position;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public BattlePlant getPlant() {
        return plant;
    }

    public String getName() {
        return name;
    }

    public boolean isBlueFiring() {
        return blueFiring;
    }

    public void setBlueFiring(boolean blueFiring) {
        this.blueFiring = blueFiring;
        if (this.firing) {
            this.damage *= 1.5;
            this.firing = false;
        }
        this.icy = false;
    }

    public Position getOffset() {
        return offset;
    }

    private void checkZombossCollision() {
        if (this.position.equals(this.target)) {
            this.isActive = false;
            destroyPlants(this.target);
        }
    }

    private void destroyPlants(Position position) {
        Position rowAndColumn = Position.getRowAndColumn(position);

        Tile target = GAME.getTileByPosition((int) rowAndColumn.getX(), (int) rowAndColumn.getY());
        for (BattlePlant plant : target.getPlants()) {
            plant.setCurrentHP(0);
        }

        if (this.name.equals("fire")) {
            spawnImpDragon(new Position(0, 0));
        }

        if (this.name.equals("missile")) {
            makeTomb();
        }

    }

    private void makeTomb() {
        Random RANDOM = new Random();
        for (int i = 0; i < 2; i++) {
            int randomRow = RANDOM.nextInt(5) + 1;
            int randomColumn = RANDOM.nextInt(9) + 1;

            Tile tombTile = GAME.getTileByPosition(randomColumn, randomRow);
            tombTile.setArable(false);
            tombTile.setHP(700);
        }
    }

    private void spawnImpDragon(Position target) {
        Position zombiePosition = new Position(600, 700);
        Zombie impDragon = ZombieFactory.createZombie("IMP_DRAGON", zombiePosition);
        GAME.addZombieFromAbility(impDragon);
    }
}
