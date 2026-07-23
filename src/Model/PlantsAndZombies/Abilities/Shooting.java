package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.*;

public class Shooting implements Ability {
    private static int TILE_Y_LENGTH = 200;
    private static int UPPER_LIMIT = 240;
    private static int BOTTOM_LIMIT = 1740;

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }
        runAbility(plant);
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
            //game.addProjectile(projectile);//todo
        }
    }

    private Projectile makeProjectile(BattlePlant plant, List<List<Integer>> directionAttributes,
                                      List<Integer> damageAttributes, int pierce,
                                      int rangeAmount, int i) {

        double velocityX = directionAttributes.get(i).get(1) * 0.5;//todo
        double velocityY = directionAttributes.get(i).get(2) * 0.5;//todo
        int damage = damageAttributes.get(i);
        if (plant.isEffected()) {
            damage *= 20;
        }
        Position position = findPosition(plant, directionAttributes.get(i).get(0));

        Projectile projectile = new Projectile(velocityX, velocityY, position,
                damage, pierce, rangeAmount);

        if (plant.getPlantStats().getTags().contains("ice")) {
            projectile.setIcy(true);
        }
        if (plant.getPlantStats().getTags().contains("poison")) {
            projectile.setPoisonous(true);
        }
        if (plant.getPlantStats().getTags().contains("fire")) {
            projectile.setFiring(true);
        }

        return projectile;
    }

    private Position findPosition(BattlePlant plant, int startingPoint) {
        int plantY = (int) plant.getPosition().getY();

        if (startingPoint == 0) {
            return plant.getPosition();
        } else if (startingPoint == 1) {
            if (plantY > UPPER_LIMIT) {
                return new Position(plant.getPosition().getX(),
                        plant.getPosition().getY() - TILE_Y_LENGTH);
            }
        } else if (startingPoint == -1) {
            if (plantY < BOTTOM_LIMIT) {
                return new Position(plant.getPosition().getX(),
                        plant.getPosition().getY() + TILE_Y_LENGTH);
            }
        }
    }

    private boolean checkTime(BattlePlant plant) {
        //todo:
        double currentTime = game.getCurrentTime();
        double timeDifference = (currentTime - plant.getEffectedTime());
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

        runAbility(plant);

        checkMegaProjectile(plant);
        checkResetting(plant);

        if (plant.getPlantStats().getTags().contains("ice")) {
            //todo;
            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition());
            int plantColumn = (int) plantRowAndColumn.getX();
            int plantRow = (int) plantRowAndColumn.getY();
            for (int i = 0; i < 9; i++) {
                Tile tile = Tile.getTile();//todo
                for (Zombie zombie : tile.getZombies()) {
                    zombie.freeze();
                }
            }
        }
        if (plant.getPlantStats().getTags().contains("fire")) {
            //todo;
            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition());
            int plantColumn = (int) plantRowAndColumn.getX();
            int plantRow = (int) plantRowAndColumn.getY();
            for (int i = 0; i < 9; i++) {
                Tile tile = Tile.getTile();//todo
                for (Zombie zombie : tile.getZombies()) {
                    zombie.unfreeze();
                }
            }
        }
        if (plant.getPlantStats().getTags().contains("charge")) {
            //todo;
            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition());
            int plantColumn = (int) plantRowAndColumn.getX();
            int plantRow = (int) plantRowAndColumn.getY();
            for (int i = 0; i < 9; i++) {
                Tile tile = Tile.getTile();//todo
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

            for (int i = 0; i < damageAttributes.size(); i++) {
                Projectile projectile = makeProjectile(plant, directionAttributes,
                        damageAttributes, pierce, rangeAmount, i);
                //game.addProjectile(projectile);//todo
            }
        }
    }

    private void checkResetting(BattlePlant plant) {
        if (plant.getPlantStats().getName().equals("SEA_SHROOM")) {
            //todo
            for (BattlePlant plant1 : game.getPlants()) {
                if (plant1.getPlantStats().getName().equals("SEA_SHROOM")) {
                    //todo:
                    plant1.setPlantTime(game.getCurrentTime());
                }
            }
        } else if (plant.getPlantStats().getName().equals("PUFF_SHROOM")) {
            //todo
            for (BattlePlant plant1 : game.getPlants()) {
                if (plant1.getPlantStats().getName().equals("PUFF_SHROOM")) {
                    //todo:
                    plant1.setPlantTime(game.getCurrentTime());
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
            if (plant.getPlantStats().getPlantFoodEffect().containsKey("knockback")) {
                int knockback = (int) plant.getPlantStats().getPlantFoodEffect().get("knockback");
                projectile.setKnockback(knockback);
            }
            //game.addProjectile(projectile);//todo
        }
    }
}
