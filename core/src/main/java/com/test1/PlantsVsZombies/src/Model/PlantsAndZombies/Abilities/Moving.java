package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.Tile;


public class Moving implements Ability {
    private static int SNORKEL_X_LIMIT = 1403;
    private static int PIANO_ACTION_INTERVAL = 20;
    private GamePlay GAME = GamePlay.activeInstance;


    private boolean isActivated = true;

    @Override
    public void executeAbility(Entity entity) {
        if (this.isActivated) {
            Zombie zombie = (Zombie) entity;
            if (zombie.isHypnotized()) {
                handleHypnotizedZombie(zombie);
                return;
            }

            moveZombie(zombie);

            if (zombie.getZombieStats().getName().equals("EXPLORER")) {
                if (zombie.getZombieStats().getAttributes().get("torch").equals("on")) {
                    handleExplorerWithIgnitedTorch(zombie);
                    return;
                }
            } else if (zombie.getZombieStats().getName().equals("PIANO")) {
                if ((GAME.getTotalTimePassed() - zombie.getLastActionTime()) >= PIANO_ACTION_INTERVAL) {
                    for (Zombie zombie1 : GAME.getGameZombies()) {
                        zombie1.changeRow();
                    }
                    zombie.setLastActionTime(GAME.getTotalTimePassed());
                }
            } else if ((zombie.getZombieStats().getName().equals("SNORKEL"))) {
                if (zombie.getPosition().getX() <= SNORKEL_X_LIMIT) {
                    zombie.setSubmarine(false);
                } else {
                    zombie.setSubmarine(true);
                }
            }

            for (BattlePlant plant : GAME.getGamePlants()) {
                Position plantPosition = plant.getPosition();

                if (zombie.getPosition().equals(plantPosition)) {
                    if (zombie.getZombieStats().getName().equals("DODO")) {
                        if (isFlyable(plant)) {
                            makeFlyingActivated(zombie);
                            return;
                        }
                    }
                    zombie.setRival(plant);

                    makeEatingActivated(zombie);
                    if (zombie.getZombieStats().getName().equals("EXPLORER")) {
                        handleExplorerTorch(zombie, plant);
                    } else if ((zombie.getZombieStats().getName().equals("SNORKEL"))) {
                        zombie.setSubmarine(false);
                    }
                    return;
                }
            }

            if (zombie.getZombieStats().getName().equals("ALL_STAR") ||
                (zombie.getZombieStats().getName().equals("TROGLOBITE")) ||
                (zombie.getZombieStats().getName().equals("ARCADE"))) {

                checkFatalDamageZombies(zombie);
            }

        }
    }

    private void handleHypnotizedZombie(Zombie zombie) {
        double velocity = zombie.getCurrentVelocity();
        double differenceX = velocity * 10;

        double zombieFinalPositionX = zombie.getPosition().getX() + differenceX;
        Position newPosition = new Position(zombieFinalPositionX, zombie.getPosition().getY());
        zombie.setPosition(newPosition);

        for (Zombie zombie1 : GAME.getGameZombies()) {
            if (zombie1.getPosition().equals(zombie.getPosition())) {
                if (!zombie1.isHypnotized()) {
                    zombie.setRival(zombie1);
                    makeEatingActivated(zombie);
                    break;
                }
            }
        }
    }

    private void moveZombie(Zombie zombie) {
        int initialColumn = zombie.getColumn();

        double velocity = zombie.getCurrentVelocity();
        double differenceX = velocity * 10;

        double zombieFinalPositionX = zombie.getPosition().getX() - differenceX;
        Position newPosition = new Position(zombieFinalPositionX, zombie.getPosition().getY());
        zombie.setPosition(newPosition);

        int finalColumn = zombie.getColumn();
        if (finalColumn != initialColumn) {
            Tile tile = GAME.getTileByPosition(zombie.getColumn(), zombie.getRow());
            if (!tile.isArable()) {
                makeFlyingActivated(zombie);
            }
        }
    }

    private void handleExplorerWithIgnitedTorch(Zombie zombie) {
        int zombieColumn = zombie.getColumn();
        int zombieRow = zombie.getRow();

        Tile tile = GAME.getTileByPosition(zombieColumn, zombieRow);

        if (tile == null)
            return;
        for (BattlePlant plant : tile.getPlants()) {
            plant.setCurrentHP(0);
        }
    }

    private void handleExplorerTorch(Zombie zombie, BattlePlant plant) {
        if (plant.getPlantStats().getTags().contains("ice")) {
            zombie.getZombieStats().getAttributes().replace("torch", "off");
        } else if (plant.getPlantStats().getTags().contains("fire")) {
            zombie.getZombieStats().getAttributes().replace("torch", "on");
        }
    }

    private void checkFatalDamageZombies(Zombie zombie) {
        for (Zombie zombie1 : GAME.getGameZombies()) {
            Position zombie1Position = zombie1.getPosition();

            if ((zombie.getPosition().equals(zombie1Position)) && (zombie1.isHypnotized())) {
                zombie.setCurrentVelocity(0);
                zombie.setRival(zombie1);
                if ((zombie.getZombieStats().getName().equals("ARCADE")) &&
                    (zombie.getActiveArmors().isEmpty())) {
                    deleteFatalDamage(zombie);
                }
                makeEatingActivated(zombie);
            }
        }
    }

    private void makeFlyingActivated(Zombie zombie) {
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof Flying) {
                ((Flying) ability).setActivated(true);
                this.isActivated = false;
                break;
            }
        }
    }

    private void makeEatingActivated(Zombie zombie) {
        this.isActivated = false;
        for (Ability ability : zombie.getOriginalAbilities()) {
            if (ability instanceof FatalDamage) {
                ((FatalDamage) ability).setActivated(true);
                zombie.setLastActionTime(GAME.getTotalTimePassed());
                return;
            } else if (ability instanceof Eating) {
                ((Eating) ability).setActivated(true);
                return;
            }
        }
    }

    private void deleteFatalDamage(Zombie zombie) {
        for (int i = 0; i < zombie.getOriginalAbilities().size(); i++) {
            if (zombie.getOriginalAbilities().get(i) instanceof FatalDamage) {
                zombie.getOriginalAbilities().remove(i);
                return;
            }
        }
    }

    public boolean isFlyable(BattlePlant plant) {
        if (plant.getPlantStats().getCategory().equals("Wall-nut")) {
            if (plant.getPlantStats().getName().equals(PlantType.TALL_NUT.getName())) {
                return false;
            }
            return true;
        } else if (plant.getPlantStats().getCategory().equals("Explosive")) {
            return true;
        } else if (plant.getPlantStats().getTags().contains("move-zombies")) {
            return true;
        }

        return false;
    }

    public void setActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public boolean isActivated() {
        return isActivated;
    }
}
