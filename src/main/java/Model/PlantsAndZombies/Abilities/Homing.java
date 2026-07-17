package src.Model.PlantsAndZombies.Abilities;

import Model.PlantsAndZombies.BattlePlant;
import Model.PlantsAndZombies.Entity;
import Model.PlantsAndZombies.Position;
import Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import Model.PlantsAndZombies.Projectiles.Projectile;
import Model.PlantsAndZombies.Zombie;
import Model.Tile;

import java.util.*;

public class Homing implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> plantTags = plant.getPlantStats().getTags();

        if (plantTags.contains("random-direction")) {
            Zombie target = findRandomZombie();
            if (target == null) {
                return;
            }

            int damage = plant.getPlantStats().getAttributes().get("damage");
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

            int damage = plant.getPlantStats().getAttributes().get("damage");
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
            int damage = plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            Projectile projectile = new Projectile(velocity.getX(), velocity.getY(),
                    plant, damage, 1);

            //todo: add projectile to game`s projectiles

        } else if (plantTags.contains("disarmament")) {
            int range = plant.getPlantStats().getAttributes().get("front-range");

            Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(), plant.getPosition().getY());
            int plantRow = (int) plantRowAndColumn.getX();
            int plantColumn = (int) plantRowAndColumn.getY();

            for (int i = 0; i <= range; i++) {
                Tile tile = getTile(); //todo: writing tile getter with row and column

                ArrayList<Zombie> zombies = tile.getZombies();//todo: getter of alive zombies in tile

                for (Zombie zombie : zombies) {
                    //todo: disarmament of zombies without any damages
                }
            }

        }


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
}
