package src.Model.PlantsAndZombies.Abilities;

import src.Model.PlantsAndZombies.*;
import src.Model.Sun.Sun;

import java.util.ArrayList;


public class WallNutAbility implements Ability {

    @Override
    public void executeAbility(Entity entity) {
        Zombie attacker = (Zombie) entity;
        BattlePlant plant = (BattlePlant) attacker.getRival();
        if (plant.isEffected()) {
            plantFoodEffect(plant);
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

                //todo: getter of tiles in game
                for (Tile tile : game.getTiles()) {
                    //todo: 1. getter of all zombies in proper range tile; 2. getter of row and column of tile
                    int distanceX = Math.abs(tile.getRow() - plantRowAndColumn.getX());
                    int distanceY = Math.abs(tile.getColumn() - plantRowAndColumn.getY());
                    if ((distanceX <= range) && (distanceY <= range)) {
                        for (Zombie zombie : tile.getAliveZombies()) {
                            zombie.setCurrentHP(zombie.getCurrentHP() - damage);
                        }
                    }
                }
            }
        }

        if (tags.contains("sun")) {
            int numberOfSun = (int) plant.getPlantStats().getAttributes().get("sun_quantity");
            Sun sun = new Sun(numberOfSun, plant.getPosition());

            //todo: increase sun amount of user
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
            plant.setCurrentHP(plant.getCurrentHP() + armor);
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
        Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition());
        int plantRow = (int) plantRowAndColumn.getY();
        int plantColumn = (int) plantRowAndColumn.getX();
        for (int i = 0; i < 9; i++) {
            Tile tile = Tile.getTile();//todo
            //todo
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
        game.getZombies().add(newGargantuar);
    }

    private void hypnotizeZombie(Zombie attacker, BattlePlant plant) {
        attacker.setHypnotized(true);

        double HPMultiplier = (double) plant.getPlantStats().getAttributes().get("HP_Buff");
        attacker.setCurrentHP(attacker.getCurrentHP() * HPMultiplier);

        double damageMultiplier = (double) plant.getPlantStats().getAttributes().get("Damage_Buff");
        attacker.getZombieStats().setEatdps(attacker.getZombieStats().getEatdps() * damageMultiplier);

    }

}
