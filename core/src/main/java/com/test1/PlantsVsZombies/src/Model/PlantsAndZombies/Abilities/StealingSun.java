package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;


public class StealingSun implements Ability {
    private static double TURQUOISE_STEAL = 2.5;
    private double stolenSun = 0;
    private boolean isActivated = false;
    private GamePlay GAME = GamePlayMenu.getGamePlay();

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        if (zombie.getZombieStats().getName().equals("TURQUOISE")) {
            int zombieColumn = zombie.getColumn();
            int zombieRow = zombie.getRow();

            if (!this.isActivated) {
                for (int i = 0; i <= 4; i++) {
                    Tile tile = GAME.getTileByPosition(zombieColumn - i, zombieRow);
                    if (tile == null) {
                        continue;
                    }
                    if (tile.getPlants() == null) {
                        continue;
                    } else {
                        zombie.setCurrentVelocity(0);
                        zombie.setLastActionTime(GAME.getTotalTimePassed());
                        this.isActivated = true;
                        changeMovingActivation(zombie, false);
                        break;
                    }
                }
            } else {
                if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) < 5) {
                    if (GAME.getMySuns() < TURQUOISE_STEAL) {
                        this.stolenSun += GAME.getMySuns();
                        GAME.setMySuns(0);
                    } else {
                        this.stolenSun += TURQUOISE_STEAL;
                        GAME.setMySuns(GAME.getMySuns() - (int) TURQUOISE_STEAL);
                    }
                } else {
                    for (int i = 0; i <= 4; i++) {
                        Tile tile = GAME.getTileByPosition(zombieColumn - i, zombieRow);
                        if (tile == null) {
                            continue;
                        }

                        for (BattlePlant plant : tile.getPlants()) {
                            plant.setAlive(false);
                        }
                    }
                    this.setActivated(false);
                    changeMovingActivation(zombie, true);
                }
            }

        } else if (zombie.getZombieStats().getName().equals("RA")) {
            for (Sun sun : GAME.getActiveSuns()) {
                sun.setCollected(true);
                this.stolenSun += sun.getNumberOfSun();
            }
        }
    }

    private void changeMovingActivation(Zombie zombie, boolean activation) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Moving) {
                ((Moving) ability).setActivated(activation);
            }
        }
    }

    public double getStolenSun() {
        return this.stolenSun;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
