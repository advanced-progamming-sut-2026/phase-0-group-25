package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Projectiles.Projectile;

import java.util.*;

public class Shooting implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;

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
            pierce = plant.getPlantStats().getAttributes().get("pierce");
        }

        for (int i = 0; i < damageAttributes.size(); i++) {
            double velocityX = directionAttributes.get(i).get(1) * 0.5;//todo
            double velocityY = directionAttributes.get(i).get(2) * 0.5;//todo
            int damage = damageAttributes.get(i);

            Projectile projectile = new Projectile(velocityX, velocityY, plant, damage, pierce, rangeAmount);
            //game.addProjectile(projectile);//todo

            if (plant.getPlantStats().getTags().contains("ice")) {
                projectile.setIcy(true);
            }
            if (plant.getPlantStats().getTags().contains("poison")) {
                projectile.setPoisonous(true);
            }

        }

    }
}
