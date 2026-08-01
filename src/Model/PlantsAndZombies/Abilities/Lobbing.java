package src.Model.PlantsAndZombies.Abilities;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.List;
import java.util.Random;

public class Lobbing implements Ability {
    private static int LOBBING_SHOT = 1820;
    private static Random RANDOM = new Random();
    private GamePlay GAME = GamePlayMenu.getGamePlay();


    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }

        List<Integer> damageAttributes = (List<Integer>) plant.getPlantStats().getAttributes().get("damage");
        List<Double> speedAttributes = (List<Double>) plant.getPlantStats().getAttributes().get("speed");

        int damage;
        double speed;

        double targetX = findNearestZombieInRow(plant);

        if (plant.getPlantStats().getAttributes().containsKey("probable")) {
            List<Double> probableAttributes = (List<Double>) plant.getPlantStats().getAttributes().get("probable");
            double roll = Math.random();

            if (roll < probableAttributes.get(0)) {
                damage = damageAttributes.get(0);
                speed = speedAttributes.get(0);
            } else {
                damage = damageAttributes.get(1);
                speed = speedAttributes.get(1);
            }
        } else {
            damage = damageAttributes.get(0);
            speed = speedAttributes.get(0);
        }

        int AoEDamage = (int) plant.getPlantStats().getAttributes().get("AoEDamage");
        int AoERange = (int) plant.getPlantStats().getAttributes().get("AoERange");

        LobbedProjectile lobbedProjectile = new LobbedProjectile(plant,
                plant.getPosition().getX(), plant.getPosition().getY(),
                targetX, speed, AoEDamage, AoERange, damage
        );

        if (plant.getPlantStats().getTags().contains("ice")) {
            lobbedProjectile.setIcy(true);
        }
        if (plant.getPlantStats().getTags().contains("fire")) {
            lobbedProjectile.setFiring(true);
        }

        GAME.getProjectiles().add(lobbedProjectile);
    }

    private boolean checkTime(BattlePlant plant) {
        double currentTime = GAME.getTotalTimePassed();
        double timeDifference = (currentTime - plant.getEffectedTime());
        if ((timeDifference % 0.6) == 0) {//every 0.6 second, lobbers execute their special ability
            return true;
        }
        return false;
    }

    private double findNearestZombieInRow(BattlePlant plant) {
        int plantColumn = plant.getColumn();
        int plantRow = plant.getRow();
        double distance = 99999;
        double targetX = LOBBING_SHOT;

        for (int i = 0; i < 9; i++) {
            Tile tile = GAME.getTileByPosition(plantColumn + i, plantRow);

            for (Zombie zombie : tile.getZombies()) {
                double tempDistance = zombie.getPosition().distance(plant.getPosition());
                if (tempDistance <= distance) {
                    distance = tempDistance;
                    targetX = zombie.getPosition().getX();
                }
            }
        }

        return targetX;
    }

    private void plantFoodEffect(BattlePlant plant) {
        List<Integer> damageAttributes = (List<Integer>) plant.getPlantStats().getAttributes().get("damage");
        List<Double> speedAttributes = (List<Double>) plant.getPlantStats().getAttributes().get("speed");

        int AoEDamage = (int) plant.getPlantStats().getAttributes().get("AoEDamage");
        int AoERange = (int) plant.getPlantStats().getAttributes().get("AoERange");

        if (plant.getPlantStats().getName().equals("KERNEL_PULT")) {
            int damage = damageAttributes.get(1);
            double speed = speedAttributes.get(1);
            for (Zombie zombie : GAME.getGameZombies()) {
                LobbedProjectile lobbedProjectile = new LobbedProjectile(plant,
                        plant.getPosition().getX(), plant.getPosition().getY(),
                        zombie.getPosition().getX(), speed,
                        AoEDamage, AoERange, damage
                );
                GAME.getProjectiles().add(lobbedProjectile);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                int randomIndex = RANDOM.nextInt(GAME.getGameZombies().size());
                Zombie zombie = GAME.getGameZombies().get(randomIndex);

                int damage = damageAttributes.get(0);
                double speed = speedAttributes.get(0);
                LobbedProjectile lobbedProjectile = new LobbedProjectile(plant,
                        plant.getPosition().getX(), plant.getPosition().getY(),
                        zombie.getPosition().getX(), speed,
                        AoEDamage, AoERange, damage
                );

                GAME.getProjectiles().add(lobbedProjectile);
            }
        }
    }
}
