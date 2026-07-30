package src.Model.PlantsAndZombies.Abilities;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Projectiles.LobbedProjectile;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.PlantsAndZombies.Zombie;

public class RepelLobbers implements Ability {
    private static GamePlay GAME = GamePlayMenu.getGamePlay();

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
