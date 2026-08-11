package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;

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

            this.isActivated = false;
            makeMovingActivated(zombie);
        }
    }

    private void makeMovingActivated(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(true);
            }
        }
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
