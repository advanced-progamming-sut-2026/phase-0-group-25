package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.List;
import java.util.Random;

public class Lobbing implements Ability {
    private static final Random RANDOM = new Random();
    private final GamePlay GAME = GamePlay.activeInstance;

    private int targetColumn = -1;
    private int targetRow = -1;

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

        if (plant.isEffected()) {
            if (checkTime(plant)) {
                plantFoodEffect(plant);
            }
            return;
        }

        Tile target = findNearestTarget(plant);

        if (target == null) {
            return;
        }

        List<Integer> damageAttributes =
            (List<Integer>) plant.getPlantStats()
                .getAttributes().get("damage");

        List<Double> speedAttributes =
            (List<Double>) plant.getPlantStats()
                .getAttributes().get("speed");

        List<String> nameAttributes =
            (List<String>) plant.getPlantStats()
                .getAttributes().get("projectileName");

        int damage = 0;
        double speed = 0;
        String name = "";

        if (plant.getPlantStats().getAttributes().containsKey("probable")) {
            List<Double> probableAttributes =
                (List<Double>) plant.getPlantStats()
                    .getAttributes().get("probable");

            double roll = Math.random();

            if (roll < probableAttributes.get(0)) {
                damage = damageAttributes.get(0);
                speed = speedAttributes.get(0);
                name = nameAttributes.get(0);
            } else {
                damage = damageAttributes.get(1);
                speed = speedAttributes.get(1);
                name = nameAttributes.get(1);
            }
        } else {
            damage = damageAttributes.get(0);
            speed = speedAttributes.get(0);
            name = nameAttributes.get(0);
        }

        int AoEDamage =
            (int) plant.getPlantStats()
                .getAttributes().get("AoEDamage");

        int AoERange =
            (int) plant.getPlantStats()
                .getAttributes().get("AoERange");

        LobbedProjectile lobbedProjectile =
            new LobbedProjectile(
                plant,
                plant.getPosition().getX(),
                plant.getPosition().getY(),
                targetColumn,
                targetRow,
                speed,
                AoEDamage,
                AoERange,
                damage,
                name
            );

        if (plant.getPlantStats().getTags().contains("ice")) {
            lobbedProjectile.setIcy(true);
        }

        if (plant.getPlantStats().getTags().contains("fire")) {
            lobbedProjectile.setFiring(true);
        }

        GAME.getProjectiles().add(lobbedProjectile);
    }

    private Tile findNearestTarget(BattlePlant plant) {
        int row = plant.getRow();
        int column = plant.getColumn();

        targetColumn = -1;
        targetRow = -1;

        for (int i = column + 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, row);

            if (tile == null) {
                continue;
            }

            if (!tile.isArable() && tile.getHP() > 0) {
                targetColumn = i;
                targetRow = row;
                return tile;
            }

            for (Zombie zombie : tile.getZombies()) {
                if (zombie.getCurrentHP() > 0) {
                    targetColumn = i;
                    targetRow = row;
                    return tile;
                }
            }

            for (BattlePlant targetPlant : tile.getPlants()) {
                if (targetPlant.equals(plant)) {
                    continue;
                }

                if (targetPlant.getCurrentHP() <= 0) {
                    continue;
                }

                if (targetPlant.isFrozen() ||
                    targetPlant.isOctopusated()) {

                    targetColumn = i;
                    targetRow = row;
                    return tile;
                }
            }
        }

        return null;
    }

    private boolean checkTime(BattlePlant plant) {
        double currentTime = GAME.getTotalTimePassed();
        double timeDifference =
            10 * (currentTime - plant.getEffectedTime());

        timeDifference = Math.floor(timeDifference);
        timeDifference /= 10;

        return (timeDifference % 0.6) == 0;
    }

    private void plantFoodEffect(BattlePlant plant) {
        List<Integer> damageAttributes =
            (List<Integer>) plant.getPlantStats()
                .getAttributes().get("damage");

        List<Double> speedAttributes =
            (List<Double>) plant.getPlantStats()
                .getAttributes().get("speed");

        List<String> nameAttributes =
            (List<String>) plant.getPlantStats()
                .getAttributes().get("projectileName");

        int AoEDamage =
            (int) plant.getPlantStats()
                .getAttributes().get("AoEDamage");

        int AoERange =
            (int) plant.getPlantStats()
                .getAttributes().get("AoERange");

        if (plant.getPlantStats().getName()
            .equals("KERNEL_PULT")) {

            int damage = damageAttributes.get(1);
            double speed = speedAttributes.get(1);
            String name = nameAttributes.get(1);

            for (Zombie zombie : GAME.getGameZombies()) {
                LobbedProjectile lobbedProjectile =
                    new LobbedProjectile(
                        plant,
                        plant.getPosition().getX(),
                        plant.getPosition().getY(),
                        zombie.getPosition().getX(),
                        zombie.getPosition().getY(),
                        speed,
                        AoEDamage,
                        AoERange,
                        damage,
                        name
                    );

                GAME.getProjectiles().add(lobbedProjectile);
            }
        } else {
            for (int i = 0; i < 3; i++) {
                if (GAME.getGameZombies().isEmpty()) {
                    return;
                }

                int randomIndex =
                    RANDOM.nextInt(
                        GAME.getGameZombies().size()
                    );

                Zombie zombie =
                    GAME.getGameZombies().get(randomIndex);

                int damage = damageAttributes.get(0);
                double speed = speedAttributes.get(0);
                String name = nameAttributes.get(0);

                LobbedProjectile lobbedProjectile =
                    new LobbedProjectile(
                        plant,
                        plant.getPosition().getX(),
                        plant.getPosition().getY(),
                        zombie.getPosition().getX(),
                        zombie.getPosition().getY(),
                        speed,
                        AoEDamage,
                        AoERange,
                        damage,
                        name
                    );

                GAME.getProjectiles().add(lobbedProjectile);
            }
        }
    }
}
