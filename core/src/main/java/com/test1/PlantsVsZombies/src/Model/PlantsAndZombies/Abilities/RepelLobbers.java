package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

public class RepelLobbers implements Ability {
    private GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        for (int i = 0; i < GAME.getProjectiles().size(); i++) {
            Projectile projectile = GAME.getProjectiles().get(i);

            if (projectile instanceof LobbedProjectile) {
                if (((LobbedProjectile) projectile).isFromLobberPlant()) {
                    projectile.setActive(false);
                    GAME.getProjectiles().remove(projectile);
                }
            }
        }

    }
}
