package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;
import src.Model.Tile;

import java.util.ArrayList;

public class Modifier implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        BattlePlant plant = (BattlePlant) entity;
        ArrayList<String> tags = plant.getPlantStats().getTags();
        if (plant.isEffected()) {
            plantFoodEffect(plant);
            return;
        }

        if (tags.contains("fire")) {
            if (!plant.isAlive()) {
                AoEDamage(plant, plant.getRow(), plant.getColumn());
                return;
            }
            //todo
            for (Projectile projectile : game.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setFiring(true);
                }
            }
        }


    }

    private void plantFoodEffect(BattlePlant plant, ArrayList<String> tags) {
        if (tags.contains("fire")) {
            //todo
            for (Projectile projectile : game.getProjectiles()) {
                if (projectile.getPosition().equals(plant.getPosition())) {
                    projectile.setDamage(projectile.getDamage() * 3);
                }
            }
        }
    }

    private void AoEDamage(BattlePlant plant, int row, int column) {
        int range = (int) plant.getPlantStats().getAttributes().get("range");
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");

        for (int i = -range; i <= range; i++) {
            for (int j = -range; j <= range; j++) {
                //todo
                Tile tile = game.getTile();
                for (Zombie zombie : tile.getZombies()) {
                    zombie.takeDamage(damage);
                }
            }
        }
    }
}
