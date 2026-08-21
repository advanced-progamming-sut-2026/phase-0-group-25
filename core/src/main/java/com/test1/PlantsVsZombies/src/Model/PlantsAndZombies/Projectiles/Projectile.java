package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieFactory;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;

public class Projectile {
    private static int X_RIGHT_LIMIT = 1820;
    protected boolean isActive;
    protected boolean icy;
    protected boolean firing;
    protected boolean poisonous;
    protected BattlePlant plant;
    private double velocityX;
    private double velocityY;
    private int damage;
    private Position position;
    private Position basePosition;
    private int pierceAmount;
    private int range;
    private int knockback;
    private boolean isHypnotizer;
    private GamePlay GAME = GamePlay.activeInstance;

    public Projectile() {

    }

    public Projectile(BattlePlant plant, double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount) {
        this.plant = plant;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.position = position;
        this.basePosition = position;

        this.damage = damage;
        this.knockback = 0;

        this.pierceAmount = pierceAmount;
        this.range = 11;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isHypnotizer = false;
    }

    public Projectile(BattlePlant plant, double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount, int range) {
        this.plant = plant;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.position = position;
        this.basePosition = position;

        this.damage = damage;
        this.knockback = 0;

        this.pierceAmount = pierceAmount;
        this.range = range;
        this.isActive = true;
        this.icy = false;
        this.firing = false;
        this.poisonous = false;
        this.isHypnotizer = false;
    }


    public void setHypnotizer(boolean isHypnotizer) {
        this.isHypnotizer = isHypnotizer;
    }

    public void update() {
        this.position = new Position((position.getX() + (0.1 * velocityX)),
                (position.getY() + (0.1 * velocityY)));

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

        if (this.position.getX() >= X_RIGHT_LIMIT) {
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
                        }
                        else if (GAME.getChapterType().equals(ChapterType.DARK_AGE)) {
                            Position dropPos = new Position(GAME.getRealX(column), GAME.getRealY(row));
                            if (tile.getGraveType() == Tile.GraveType.PLANT_FOOD) {
                                GAME.glowingAward(dropPos);
                            } else if (tile.getGraveType() == Tile.GraveType.SUN) {
                                GAME.getActiveSuns().add(new Sun(50, dropPos, 0));
                            }
                            System.out.println("Dark Age tomb destroyed!");
                        }
                        else {
                            System.out.println("Tomb destroyed!!!!!");
                        }
                    }
                    this.isActive = false;
                }
            }
        }
    }

    public void checkCollision() {
        for (Zombie zombie : GAME.getGameZombies()) {
            if (!zombie.isHypnotized()) {
                if (this.position.equals(zombie.getPosition())) {
                    if ((zombie.getZombieStats().getName().equals("SNORKEL")) &&
                            (zombie.getZombieStats().getAttributes().get("submarine").equals("on"))) {
                        continue;
                    }
                    zombie.takeDamage(this, this.damage);
                    zombie.setPosition(new Position(
                            zombie.getPosition().getX() + this.knockback,
                            zombie.getPosition().getY()));

                    this.setPierceAmount(this.getPierceAmount() - 1);
                }
            }
        }
        for (BattlePlant plant : GAME.getPlants()) {
            if (this.firing) {
                plant.setFrozen(false);
            } else {
                if (plant.isFrozen()) {
                    plant.takeIceDamage(this.damage);
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
        if (this.pierceAmount == 0) {
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
}
