package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Abilities;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;


public class WallNutAbility implements Ability {
    private GamePlay GAME = GamePlay.activeInstance;

    @Override
    public void executeAbility(Entity entity) {
        Zombie attacker = (Zombie) entity;
        BattlePlant plant = (BattlePlant) attacker.getRival();
        if (plant.isEffected()) {
            plantFoodEffect(attacker, plant);
        }

        ArrayList<String> tags = plant.getPlantStats().getTags();

        if (tags.contains("reflection")) {
            int damage = checkEffected(plant);

            attacker.setCurrentHP(attacker.getCurrentHP() - damage);
            plant.setCurrentHP(plant.getCurrentHP()
                + attacker.getZombieStats().getEatdps());
        }
        if (tags.contains("move-zombies")) {
            if ((int) plant.getPlantStats().getAttributes().get("move") == 1) {
                if (!plant.isAlive()) {
                    if (plant.isEffected()) {
                        repelZombiesInEffected(plant);
                        return;
                    }
                    attacker.changeRow();
                }
            }
        }

        if (tags.contains("explosion")) {
            if (!plant.isAlive()) {
                Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(), plant.getPosition().getY());
                int range = (int) plant.getPlantStats().getAttributes().get("range");
                int damage = checkEffected(plant);

                for (Tile tile : GAME.getTiles()) {
                    int distanceX = Math.abs((int) tile.getPosition().getX() - (int) plantRowAndColumn.getX());
                    int distanceY = Math.abs((int) tile.getPosition().getY() - (int) plantRowAndColumn.getY());
                    if ((distanceX <= range) && (distanceY <= range)) {
                        for (Zombie zombie : tile.getZombies()) {
                            zombie.setCurrentHP(zombie.getCurrentHP() - damage);
                        }
                    }
                }
            }
        }

        if (tags.contains("sun")) {
            int numberOfSun = (int) plant.getPlantStats().getAttributes().get("sun_quantity");

            GAME.setMySuns(GAME.getMySuns() + numberOfSun);
        }

        if (tags.contains("shroom")) {
            if (!plant.isAlive()) {
                hypnotizeZombie(attacker, plant);
            }
        }
    }

    private void plantFoodEffect(Zombie attacker, BattlePlant plant) {
        if (plant.getPlantStats().getTags().contains("shroom")) {
            makeGargantuar(attacker, plant);
        }

        if (!plant.getPlantStats().getTags().contains("moveZombies")) {
            int armor = (int) plant.getPlantStats().getPlantFoodEffect().get("armor");
            plant.getPlantStats().getAttributes().put("armorHP", armor);

            plant.setCurrentHP(plant.getPlantStats().getBaseHP());
        }
    }

    private int checkEffected(BattlePlant plant) {
        int damage = (int) plant.getPlantStats().getAttributes().get("damage");
        if (plant.isEffected()) {
            damage *= (int) plant.getPlantStats().getPlantFoodEffect().get("damageMultiplier");
        }
        return damage;
    }

    private void repelZombiesInEffected(BattlePlant plant) {

        int plantRow = plant.getRow();
        for (int i = 1; i <= 9; i++) {
            Tile tile = GAME.getTileByPosition(i, plantRow);

            for (Zombie zombie : tile.getZombies()) {
                zombie.changeRow();
            }
        }
    }

    private void makeGargantuar(Zombie attacker, BattlePlant plant) {
        Position attackerPosition = attacker.getPosition();
        attacker.setCurrentHP(0);

        Zombie newGargantuar = ZombieFactory.createZombie("GARGANTUAR", attackerPosition);
        newGargantuar.setHypnotized(true);
        //todo
        GAME.getGameZombies().add(newGargantuar);
    }

    private void hypnotizeZombie(Zombie attacker, BattlePlant plant) {
        attacker.setHypnotized(true);

        double HPMultiplier = (double) plant.getPlantStats().getAttributes().get("HP_Buff");
        attacker.setCurrentHP(attacker.getCurrentHP() * HPMultiplier);

        double damageMultiplier = (double) plant.getPlantStats().getAttributes().get("Damage_Buff");
        attacker.getZombieStats().setEatdps(attacker.getZombieStats().getEatdps() * damageMultiplier);

    }

}
