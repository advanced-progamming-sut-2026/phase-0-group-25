package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.*;

public class Homing implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> plantTags = plant.getPlantStats().getTags();
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant, plantTags);
            }
            return;
        }

        runAbility(plant, plantTags);
    }

    private boolean checkTime(BattlePlant plant) {
        //todo:
        double currentTime = game.getCurrentTime();
        double timeDifference = (currentTime - plant.getEffectedTime());
        if ((timeDifference % 0.4) == 0) {//every 0.4 second, homings execute their special ability
            return true;
        }
        return false;
    }


    public Zombie findRandomZombie() {
        //todo: writing a function which gives all alive zombies in game
        ArrayList<Zombie> zombies = game.getAliveZombies();
        if (zombies.isEmpty()) {
            return null;
        }
        Collections.shuffle(zombies);
        return zombies.get(0);
    }

    public Zombie findTheHealthiestZombie() {
        Zombie target = null;
        //todo: writing a function which gives all alive zombies in game
        if (game.getAliveZombies().isEmpty()) {
            return null;
        }

        for (Zombie zombie : game.getAliveZombies()) {
            if (target == null) {
                target = zombie;
            } else {
                if (zombie.getCurrentHP() > target.getCurrentHP()) {
                    target = zombie;
                }
            }
        }

        return target;
    }

    public Zombie findTheNearestZombie(Position plantPosition) {
        Zombie target = null;
        double distance = 1000000000;

        //todo: writing a function which gives all alive zombies in game
        if (game.getAliveZombies().isEmpty()) {
            return null;
        }

        for (Zombie zombie : game.getAliveZombies()) {
            if (target == null) {
                target = zombie;
            } else {
                Position zombiePosition = zombie.getPosition();
                double tempDistance = findDistance(plantPosition, zombiePosition);

                if (tempDistance <= distance) {
                    target = zombie;
                }
            }
        }

        return target;
    }

    public double findDistance(Position plantPosition, Position zombiePosition) {
        double xDistance = Math.pow((plantPosition.getX() - zombiePosition.getX()), 2);
        double yDistance = Math.pow((plantPosition.getY() - zombiePosition.getY()), 2);

        return xDistance + yDistance;
    }

    public Position findVelocity(Position plantPosition, Position targetPosition) {
        double xDistance = plantPosition.getX() - targetPosition.getX();
        double yDistance = plantPosition.getY() - targetPosition.getY();

        if (xDistance == 0) {
            return new Position(0, 1);
        } else if (yDistance == 0) {
            return new Position(1, 0);
        } else {
            double hypotenuse = Math.hypot(xDistance, yDistance);

            return new Position((xDistance / hypotenuse), (yDistance / hypotenuse));
        }

    }

    private void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("disarmament")) {
            runAbility(plant, tags);
            return;
        }

        if (tags.contains("directed")) {
            runAbility(plant, tags);
            return;
        }

        Map<String, Object> plantFoodEffect = plant.getPlantStats().getPlantFoodEffect();
        int number = (int) plantFoodEffect.get("number");

        for (int i = 0; i < number; i++) {
            Zombie target = findRandomZombie();
            if (target == null) {
                return;
            }

            if (tags.contains("hypnotize")) {
                target.setHypnotized(true);
            } else {
                target.setCurrentHP(0);
            }

        }
    }

    private void runAbility(BattlePlant plant, ArrayList<String> plantTags) {
        if (plantTags.contains("random-direction")) {
            Zombie target = findRandomZombie();
            if (target == null) {
                return;
            }

            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            LobbedProjectile lobbedProjectile = new LobbedProjectile(
                    plant.getPosition().getX(), plant.getPosition().getY(),
                    target.getPosition().getX(), target.getPosition().getY(),
                    1, 0, 0, damage);
            if (plantTags.contains("hypnotize")) {
                lobbedProjectile.setHypnotizer(true);
            }
            //todo: add lobbed projectile to game`s projectiles

        } else if (plantTags.contains("healthiest")) {
            Zombie target = findTheHealthiestZombie();
            if (target == null) {
                return;
            }

            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            LobbedProjectile lobbedProjectile = new LobbedProjectile(
                    plant.getPosition().getX(), plant.getPosition().getY(),
                    target.getPosition().getX(), target.getPosition().getY(),
                    1, 0, 0, damage);
            //todo: add lobbed projectile to game`s projectiles

        } else if (plantTags.contains("directed")) {
            Zombie target = findTheNearestZombie(plant.getPosition());
            if (target == null) {
                return;
            }
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            Projectile projectile = new Projectile(velocity.getX(), velocity.getY(),
                    plant.getPosition(), damage, 1);
            //todo: add projectile to game`s projectiles

        } else if (plantTags.contains("disarmament")) {
            int range = (int) plant.getPlantStats().getAttributes().get("front-range");

            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(), plant.getPosition().getY());
            int plantRow = (int) plantRowAndColumn.getX();
            int plantColumn = (int) plantRowAndColumn.getY();

            for (int i = 0; i <= range; i++) {
                Tile tile = getTile(); //todo: writing tile getter with row and column
                ArrayList<Zombie> zombies = tile.getZombies();//todo: getter of alive zombies in tile

                for (Zombie zombie : zombies) {
                    zombie.disarmament();
                }
            }
        }
    }
}
