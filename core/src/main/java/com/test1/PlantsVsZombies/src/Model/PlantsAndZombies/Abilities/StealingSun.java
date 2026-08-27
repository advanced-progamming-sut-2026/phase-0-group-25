package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Sun.Sun;
import com.test1.PlantsVsZombies.src.Model.Tile;


public class StealingSun implements Ability {
    private static double TURQUOISE_STEAL = 3;
    private double stolenSun = 0;
    private boolean isActivated = false;
    private GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        if (zombie.getZombieStats().getName().equals("TURQUOISE")) {
            int zombieColumn = zombie.getColumn();
            int zombieRow = zombie.getRow();

            if (!this.isActivated) {
                for (int i = 0; i < 4; i++) {
                    Tile tile = GAME.getTileByPosition(zombieColumn - i, zombieRow);
                    if (tile == null) {
                        continue;
                    }
                    if (tile.getPlants().isEmpty()) {
                        continue;
                    } else {
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
                    for (int i = 0; i < 4; i++) {
                        Tile tile = GAME.getTileByPosition(zombieColumn - i, zombieRow);
                        if (tile == null) {
                            continue;
                        }

                        for (BattlePlant plant : tile.getPlants()) {
                            plant.setAlive(false);
                            plant.setCurrentHP(0);
                        }
                    }
                    this.isActivated = false;
                    changeMovingActivation(zombie, true);
                }
            }

        } else if (zombie.getZombieStats().getName().equals("RA")) {
            if (this.isActivated) {
                double actionTime = (double) zombie.getZombieStats().getAttributes().get("actionTime");
                float difference = (float) (GAME.getTotalTimePassed() - zombie.getLastActionTime());

                if (difference > actionTime) {

                    int activeSun = GAME.getActiveSuns().size();
                    for (int i = 0; i < activeSun; i++) {
                        try {
                            Sun sun = GAME.getActiveSuns().get(i);
                            sun.setCollected(true);
                            if (!sun.isFromSky()) {
                                Position sunPosition = Position.getRowAndColumn(sun.getPosition().getX() - 20, sun.getPosition().getY() - 20);
                                Tile tile = GAME.getTileByPosition((int) sunPosition.getX(), (int) sunPosition.getY());


                                BattlePlant sunProducer = null;
                                for (BattlePlant plant : tile.getPlants()) {
                                    if (plant.getPlantStats().getCategory().equals("Sun Producer")) {
                                        sunProducer = plant;
                                        break;
                                    }
                                }
                                Ability ability = tile.getPlants().get(0).getOriginalAbilities().get(0);
                                if (ability instanceof ProducingSun) {
                                    ((ProducingSun) ability).setCollected(false);
                                    ((ProducingSun) ability).setProduced(false);
                                }
                            }
                            this.stolenSun += sun.getNumberOfSun();
                            GAME.getActiveSuns().remove(i);
                            i -= 1;
                        } catch (IndexOutOfBoundsException e) {

                        }
                    }
                    this.isActivated = false;
                    changeMovingActivation(zombie, true);
                }
            } else {

                if (!GAME.getActiveSuns().isEmpty()) {
                    zombie.setLastActionTime(GAME.getTotalTimePassed());
                    this.isActivated = true;
                    changeMovingActivation(zombie, false);
                }

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
