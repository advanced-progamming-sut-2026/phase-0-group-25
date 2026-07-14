package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;

import java.util.List;
import java.util.Map;

public class Lobbing implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

        List<Integer> damageAttributes = (List<Integer>) (Object) plant.getPlantStats().getAttributes().get("damage");
        List<Integer> speedAttributes = (List<Integer>) (Object) plant.getPlantStats().getAttributes().get("speed");

        int damage;
        double speed;

        double targetX = findNearestZombieInRow(plant.getPosition().getX(), plant.getPosition().getY());//todo

        if (plant.getPlantStats().getAttributes().containsKey("probable")) {
            List<Double> probableAttributes = (List<Double>) (Object) plant.getPlantStats().getAttributes().get("probable");
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

        int AoEDamage = plant.getPlantStats().getAttributes().get("AoEDamage");
        int AoERange = plant.getPlantStats().getAttributes().get("AoERange");

        LobbedProjectile lobbedProjectile = new LobbedProjectile(
                plant.getPosition().getX(), plant.getPosition().getY(),
                targetX, speed, AoEDamage, AoERange, damage
        );

        if (plant.getPlantStats().getTags().contains("ice")) {
            lobbedProjectile.setIcy(true);
        }

        //game.addProjectile(lobbedProjectile);
    }
}
