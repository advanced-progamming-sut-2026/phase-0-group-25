package src.Model.PlantsAndZombies.Projectiles;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;

public class Projectile {
    protected boolean isActive;
    protected boolean icy;
    protected boolean firing;
    private double velocityX;
    private double velocityY;
    private int damage;
    private Position position;
    private Position basePosition;
    private int pierceAmount;
    private int range;
    private int knockback;
    private boolean poisonous;
    private boolean isHypnotizer;

    private static GamePlay GAME = GamePlayMenu.getGamePlay();

    public Projectile() {

    }

    public Projectile(double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount) {
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
    }

    public Projectile(double velocityX, double velocityY, Position position,
                      int damage, int pierceAmount, int range) {
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
    }


    public void setHypnotizer(boolean isHypnotizer) {
        this.isHypnotizer = isHypnotizer;
    }

    public void update() {
        position.setX(position.getX() + (0.1 * velocityX));
        position.setY(position.getY() + (0.1 * velocityY));


        updateActivation();

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
                plant.setIceTime(0);
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
}
