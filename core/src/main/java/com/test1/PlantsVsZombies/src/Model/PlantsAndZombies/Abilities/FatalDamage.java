package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;

public class FatalDamage implements Ability {
    private boolean isActivated = false;
    private GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        if (this.isActivated) {
            Zombie zombie = (Zombie) entity;
            Entity rival = zombie.getRival();

            if (zombie.getZombieStats().getName().equals("ALL_STAR")) {
                zombie.setCurrentVelocity(zombie.getZombieStats().getVelocity() / 2);
            }

            double fatalTime = (double) zombie.getZombieStats().getAttributes().get("fatalTime");
            float difference = (float) (GAME.getTotalTimePassed() - zombie.getLastActionTime());

            System.out.println(fatalTime + "   " + difference);


            if (fatalTime < difference) {
                double zombieFinalPositionX = zombie.getPosition().getX() - 10;
                Position newPosition = new Position(zombieFinalPositionX, zombie.getPosition().getY());
                zombie.setPosition(newPosition);


                rival.setCurrentHP(0);
                rival.setAlive(false);
                this.isActivated = false;
                makeMovingActivated(zombie);
            }
        }
    }

    private void makeMovingActivated(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(true);
            }
        }
    }

    public void setActivated(boolean activated) {
        this.isActivated = activated;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
