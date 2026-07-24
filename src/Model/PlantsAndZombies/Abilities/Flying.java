package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.Position;
import src.Model.PlantsAndZombies.Zombie;

public class Flying implements Ability {
    private static int TILE_X_LENGTH = 200;
    private boolean isActivated = false;

    @Override
    public void executeAbility(Entity entity) {
        if (this.isActivated) {
            Zombie zombie = (Zombie) entity;

            zombie.setPosition(new Position(
                    zombie.getPosition().getX() - TILE_X_LENGTH,
                    zombie.getPosition().getY()
            ));
            //todo: activate moving
        }

    }
}
