package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.List;

public class Shooting implements Ability {
    private static int TILE_Y_LENGTH = 150;
    private static int X_RIGHT_LIMIT = 1860;
    private static int Y_UP_LIMIT = 880;
    private static int Y_DOWN_LIMIT = 130;
    private static int X_LEFT_LIMIT = 490;

    private GamePlay GAME = GamePlay.activeInstance;
    private BattlePlant plant;
    private int startingPoint;


    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }
        if (haveTarget(plant)) {
            runAbility(plant);
        }
    }

    private void runAbility(BattlePlant plant) {
        List<Integer> damageAttributes = (List<Integer>) (Object) plant.getPlantStats().getAttributes().get("damage");
        List<List<Integer>> directionAttributes = (List<List<Integer>>) (Object) plant.getPlantStats().getAttributes().get("direction");
        int rangeAmount;
        int pierce = 1;

        if (plant.getPlantStats().getAttributes().containsKey("range")) {
            rangeAmount = (int) plant.getPlantStats().getAttributes().get("range");
        } else {
            rangeAmount = 11;
        }

        if (plant.getPlantStats().getAttributes().containsKey("pierce")) {
            pierce = (int) plant.getPlantStats().getAttributes().get("pierce");
        }

        for (int i = 0; i < damageAttributes.size(); i++) {
            Projectile projectile = makeProjectile(plant, directionAttributes,
                damageAttributes, pierce, rangeAmount, i);
            if (projectile != null) {
                GAME.getProjectiles().add(projectile);
            }
        }
    }

    private Projectile makeProjectile(BattlePlant plant, List<List<Integer>> directionAttributes,
                                      List<Integer> damageAttributes, int pierce,
                                      int rangeAmount, int i) {

        double velocityX = directionAttributes.get(i).get(1) * 50;//todo
        double velocityY = directionAttributes.get(i).get(2) * 50;//todo
        int damage = damageAttributes.get(i);

        Position position = findPosition(plant, directionAttributes.get(i).get(0));
        if ((position.getX() <= X_LEFT_LIMIT) ||
            (position.getX() >= X_RIGHT_LIMIT) ||
            (position.getY() <= Y_DOWN_LIMIT) ||
            (position.getY() >= Y_UP_LIMIT)) {
            return null;
        }

        Projectile projectile = new Projectile(plant, velocityX, velocityY, position,
            damage, pierce, rangeAmount);

        if (plant.getPlantStats().getTags().contains("pea")) {
            if (plant.getPlantStats().getTags().contains("ice")) {
                projectile.setIcy(true);
            }
            if (plant.getPlantStats().getTags().contains("fire")) {
                projectile.setFiring(true);
            }
        }
        if (plant.getPlantStats().getTags().contains("poison")) {
            projectile.setPoisonous(true);
        }


        return projectile;
    }

    private Projectile makeProjectile(BattlePlant plant, List<List<Integer>> directionAttributes,
                                      List<Integer> damageAttributes, int pierce,
                                      int rangeAmount, int i, String name) {

        double velocityX = directionAttributes.get(i).get(1) * 50;//todo
        double velocityY = directionAttributes.get(i).get(2) * 50;//todo
        int damage = damageAttributes.get(i);
        if (plant.isEffected()) {
            damage *= 20;
        }
        Position position = findPosition(plant, directionAttributes.get(i).get(0));
        if ((position.getX() <= X_LEFT_LIMIT) ||
            (position.getX() >= X_RIGHT_LIMIT) ||
            (position.getY() <= Y_DOWN_LIMIT) ||
            (position.getY() >= Y_UP_LIMIT)) {
            return null;
        }

        Projectile projectile = new Projectile(plant, velocityX, velocityY, position,
            damage, pierce, rangeAmount, name);

        if (plant.getPlantStats().getTags().contains("pea")) {
            if (plant.getPlantStats().getTags().contains("ice")) {
                projectile.setIcy(true);
            }
            if (plant.getPlantStats().getTags().contains("fire")) {
                projectile.setFiring(true);
            }
        }
        if (plant.getPlantStats().getTags().contains("poison")) {
            projectile.setPoisonous(true);
        }


        return projectile;
    }

    private Position findPosition(BattlePlant plant, double startingPoint) {
        // this.plant = plant;
        //this.startingPoint = (int) startingPoint;

        if (startingPoint == 1) {
            return new Position(plant.getPosition().getX(),
                plant.getPosition().getY() + TILE_Y_LENGTH);
        } else if (startingPoint == -1) {
            return new Position(plant.getPosition().getX(),
                plant.getPosition().getY() - TILE_Y_LENGTH);
        }

        return plant.getPosition();
    }

    private boolean checkTime(BattlePlant plant) {

        double currentTime = GAME.getTotalTimePassed();
        double timeDifference = 10 * (currentTime - plant.getEffectedTime());
        timeDifference = Math.floor(timeDifference);
        timeDifference /= 10;
        if ((timeDifference % 0.5) == 0) {//every 0.5 second, shooters & strike-throughs execute their special ability
            return true;
        }
        return false;
    }

    private void plantFoodEffect(BattlePlant plant) {
        if (plant.getPlantStats().getCategory().equals("Strike-through")) {
            strike_throughPlantFoodEffect(plant);
            return;
        }

        if (plant.getName().equals(PlantType.THREEPEATER.getName())) {
            effectedThreepeter(plant);
            return;
        }

        runAbility(plant);

        double difference = GAME.getTotalTimePassed() - plant.getEffectedTime();
        if (Math.abs(difference - (plant.getEffectedLifeSpan() / 2)) <= 0.1) {
            checkMegaProjectile(plant);
            checkResetting(plant);
        }

        if (plant.getPlantStats().getTags().contains("ice")) {

            int plantRow = plant.getRow();
            for (int i = 1; i <= 9; i++) {
                Tile tile = GAME.getTileByPosition(i, plantRow);

                for (Zombie zombie : tile.getZombies()) {
                    zombie.freeze();
                }
                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(true);
                }
            }
        }
        if (plant.getPlantStats().getTags().contains("fire")) {

            int plantRow = plant.getRow();
            for (int i = 1; i <= 9; i++) {
                Tile tile = GAME.getTileByPosition(i, plantRow);
                if (tile == null) {
                    return;
                }

                for (Zombie zombie : tile.getZombies()) {
                    zombie.unfreeze();
                }

                for (BattlePlant plant1 : tile.getPlants()) {
                    plant1.setFrozen(false);
                }
            }
        }
        if (plant.getPlantStats().getTags().contains("charge")) {

            int plantRow = plant.getRow();
            for (int i = 1; i <= 9; i++) {
                Tile tile = GAME.getTileByPosition(i, plantRow);
                if (tile == null) {
                    return;
                }
                for (Zombie zombie : tile.getZombies()) {
                    zombie.setCurrentHP(0);
                }
            }
        }
    }

    private void checkMegaProjectile(BattlePlant plant) {
        if (plant.getPlantStats().getPlantFoodEffect().containsKey("megaProjectile")) {
            List<Integer> damageAttributes = (List<Integer>) (Object) plant.getPlantStats().getAttributes().get("damage");
            List<List<Integer>> directionAttributes = (List<List<Integer>>) (Object) plant.getPlantStats().getAttributes().get("direction");
            int rangeAmount = 11;
            int pierce = 1;


            Projectile projectile = makeProjectile(plant, directionAttributes,
                damageAttributes, pierce, rangeAmount, 0, "mega pea");
            if (projectile != null) {
                GAME.getProjectiles().add(projectile);
            }
        }
    }


    private void checkResetting(BattlePlant plant) {
        if (plant.getPlantStats().getName().equals("SEA_SHROOM")) {
            for (BattlePlant plant1 : GAME.getPlants()) {
                if (plant1.getPlantStats().getName().equals("SEA_SHROOM")) {
                    plant1.setPlantTime(GAME.getTotalTimePassed());
                }
            }
        } else if (plant.getPlantStats().getName().equals("PUFF_SHROOM")) {
            for (BattlePlant plant1 : GAME.getPlants()) {
                if (plant1.getPlantStats().getName().equals("PUFF_SHROOM")) {
                    plant1.setPlantTime(GAME.getTotalTimePassed());
                }
            }
        }
    }

    private void strike_throughPlantFoodEffect(BattlePlant plant) {
        List<Integer> damageAttributes = (List<Integer>) (Object) plant.getPlantStats().getPlantFoodEffect().get("damage");
        List<List<Integer>> directionAttributes = (List<List<Integer>>) (Object) plant.getPlantStats().getPlantFoodEffect().get("direction");
        int rangeAmount;
        int pierce = 1;

        if (plant.getPlantStats().getPlantFoodEffect().containsKey("range")) {
            rangeAmount = (int) plant.getPlantStats().getPlantFoodEffect().get("range");
        } else {
            rangeAmount = 11;
        }

        if (plant.getPlantStats().getPlantFoodEffect().containsKey("pierce")) {
            pierce = (int) plant.getPlantStats().getPlantFoodEffect().get("pierce");
        }


        for (int i = 0; i < damageAttributes.size(); i++) {
            Projectile projectile = makeProjectile(plant, directionAttributes,
                damageAttributes, pierce, rangeAmount, i);
            if (projectile != null) {
                if (plant.getPlantStats().getPlantFoodEffect().containsKey("knockback")) {
                    int knockback = (int) plant.getPlantStats().getPlantFoodEffect().get("knockback");
                    projectile.setKnockback(knockback);
                }
                GAME.getProjectiles().add(projectile);
            }
        }
    }

    private boolean haveTarget(BattlePlant plant) {
        if (plant.getName().equals(PlantType.CITRON.getName())) {
            return true;
        }

        int row = plant.getRow();
        int column = plant.getColumn();

        for (int i = column; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, row);
            if (!tile.getZombies().isEmpty()) {
                return true;
            }

            if (GAME.getChapterType().equals(ChapterType.ANCIENT_EGYPT) ||
                GAME.getChapterType().equals(ChapterType.DARK_AGE) ||
                GAME.getChapterType().equals(ChapterType.FROSTBITE_CAVES)) {
                if ((!tile.isArable()) && (tile.getHP() > 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void effectedThreepeter(BattlePlant plant) {
        for (int i = 1; i <= 5; i++) {
            Position position = new Position(plant.getPosition().getX(), (i - 1) * 150 + 205);
            Projectile projectile = new Projectile(plant, 50, 0, position, 20, 1);
            GAME.getProjectiles().add(projectile);
        }
    }
}
