package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class Homing implements Ability {
    private final GamePlay GAME = GamePlay.activeInstance;

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
        double currentTime = GAME.getTotalTimePassed();
        double timeDifference = 10 * (currentTime - plant.getEffectedTime());
        timeDifference = Math.floor(timeDifference);
        timeDifference /= 10;
        //every 0.4 second, homings execute their special ability
        return (timeDifference % 0.4) == 0;
    }


    public Zombie findRandomZombie() {
        ArrayList<Zombie> zombies = GAME.getGameZombies();
        if (zombies.isEmpty()) {
            return null;
        }
        Collections.shuffle(zombies);
        return zombies.get(0);
    }

    public Zombie findTheHealthiestZombie() {
        Zombie target = null;
        if (GAME.getGameZombies().isEmpty()) {
            return null;
        }

        for (Zombie zombie : GAME.getGameZombies()) {
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
        double distance = 5000;

        if (GAME.getGameZombies().isEmpty()) {
            return null;
        }

        for (Zombie zombie : GAME.getGameZombies()) {
            if (target == null) {
                target = zombie;
                double tempDistance = findDistance(plantPosition, zombie.getPosition());
                distance = tempDistance;
            } else {
                Position zombiePosition = zombie.getPosition();
                double tempDistance = findDistance(plantPosition, zombiePosition);

                if (tempDistance <= distance) {
                    target = zombie;
                    distance = tempDistance;
                }
            }
        }

        return target;
    }

    public double findDistance(Position plantPosition, Position zombiePosition) {
        double xDistance = Math.abs((plantPosition.getX() - zombiePosition.getX()));
        double yDistance = Math.abs((plantPosition.getY() - zombiePosition.getY()));

        return Math.hypot(xDistance, yDistance);
    }

    public Position findVelocity(Position plantPosition, Position targetPosition) {
        double xDistance = targetPosition.getX() - plantPosition.getX();
        double yDistance = targetPosition.getY() - plantPosition.getY();

        if (xDistance == 0) {
            return new Position(0, 50);
        } else if (yDistance == 0) {
            return new Position(50, 0);
        } else {
            double hypotenuse = Math.hypot(xDistance, yDistance);

            return new Position((xDistance / hypotenuse) * 50, (yDistance / hypotenuse) * 50);
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

    private boolean isNotArmored(BattlePlant plant) {
        if (plant.getPlantStats().getTags().contains("charge")) {
            int armTime = (int) plant.getPlantStats().getAttributes().get("armTime");
            return (GAME.getTotalTimePassed() - plant.getPlantTime()) < armTime;
        }

        return false;
    }

    private void runAbility(BattlePlant plant, ArrayList<String> plantTags) {
        if (isNotArmored(plant)) {
            return;
        }

        if (plantTags.contains("random-direction")) {
            Zombie target = findRandomZombie();
            if (target == null) {
                return;
            }

            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            String projectileName = (String) plant.getPlantStats().getAttributes().get("projectileName");
            LobbedProjectile lobbedProjectile = new LobbedProjectile(plant,
                plant.getPosition().getX(), plant.getPosition().getY(),
                target.getPosition().getX(), target.getPosition().getY(),
                1, 0, 0, damage, projectileName);
            if (plantTags.contains("hypnotize")) {
                lobbedProjectile.setHypnotizer(true);
            }
            GAME.getProjectiles().add(lobbedProjectile);

        } else if (plantTags.contains("healthiest")) {
            Zombie target = findTheHealthiestZombie();
            if (target == null) {
                return;
            }

            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            String projectileName = (String) plant.getPlantStats().getAttributes().get("projectileName");
            LobbedProjectile lobbedProjectile = new LobbedProjectile(plant,
                plant.getPosition().getX(), plant.getPosition().getY(),
                target.getPosition().getX(), target.getPosition().getY(),
                1, 0, 0, damage, projectileName);
            if (plantTags.contains("hypnotize")) {
                lobbedProjectile.setHypnotizer(true);
            }
            GAME.getProjectiles().add(lobbedProjectile);

        } else if (plantTags.contains("directed")) {
            Zombie target = findTheNearestZombie(plant.getPosition());
            if (target == null) {
                return;
            }
            int damage = (int) plant.getPlantStats().getAttributes().get("damage");
            Position velocity = findVelocity(plant.getPosition(), target.getPosition());
            Projectile projectile = new Projectile(plant, velocity.getX(), velocity.getY(),
                plant.getPosition(), damage, 1, 0, 0);

            GAME.getProjectiles().add(projectile);
        } else if (plantTags.contains("disarmament")) {
            if (plant.isEffected()) {
                for (Tile tile : GAME.getTiles()) {
                    for (Zombie zombie : tile.getZombies()) {
                        zombie.disarmament();
                    }
                }
            }
            int range = (int) plant.getPlantStats().getAttributes().get("front-range");

            int plantRow = plant.getRow();
            int plantColumn = plant.getColumn();

            for (int i = 0; i <= range; i++) {
                Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow);
                if (tile == null) {
                    continue;
                }

                for (Zombie zombie : tile.getZombies()) {
                    zombie.disarmament();
                }
            }
        }
    }
}
