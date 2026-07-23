package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.Armors.Armor;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;

import java.util.ArrayList;

public class RepelLobbers implements Ability {
    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        //todo: a function which gives all active projectiles
        for (int i = 0; i < game.getProjectiles(); i++) {
            Projectile projectile = game.getProjectiles().get(i);

            if (projectile instanceof LobbedProjectile) {
                if (((LobbedProjectile) projectile).isFromLobberPlant()) {
                    projectile.setActive(false);
                    game.getProjectiles().remove(projectile);
                }
            }
        }

    }
}
