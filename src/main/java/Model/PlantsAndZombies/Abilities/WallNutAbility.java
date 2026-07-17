package Model.PlantsAndZombies.Abilities;

import Model.PlantsAndZombies.BattlePlant;
import Model.PlantsAndZombies.Position;
import Model.PlantsAndZombies.Tile;
import Model.PlantsAndZombies.Zombie;
import Model.Sun.Sun;

import java.util.ArrayList;


public class WallNutAbility {
    private BattlePlant plant;
    private Zombie attacker;

    public WallNutAbility(BattlePlant plant, Zombie attacker) {
        this.plant = plant;
        this.attacker = attacker;
    }

    public void execute() {
        ArrayList<String> tags = plant.getPlantStats().getTags();
        //todo: get zombie`s damage
        plant.setCurrentHP(plant.getCurrentHP() - attacker.getDamage());


        if (tags.contains("reflection")) {
            //todo: get zombie`s damage
            attacker.setCurrentHP(attacker.getCurrentHP() - attacker.getDamage());
            plant.setCurrentHP(plant.getCurrentHP() + attacker.getDamage());
        }
        if (tags.contains("move-zombies")) {
            if (plant.getPlantStats().getAttributes().get("move") == 1) {
                if (!plant.isAlive()) {
                    Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(), plant.getPosition().getY());



                }
            } else if (plant.getPlantStats().getAttributes().get("move") == -1 ) {

            }
        }

        if (tags.contains("explosion")) {
            if (!plant.isAlive()) {
                Position plantRowAndColumn = Position.getRowAndColumn(plant.getPosition().getX(), plant.getPosition().getY());
                int range = plant.getPlantStats().getAttributes().get("range");

                //todo: getter of tiles in game
                for (Tile tile : game.getTiles()) {
                    //todo: 1. getter of all zombies in proper range tile; 2. getter of row and column of tile
                    int distanceX = Math.abs(tile.getRow() - plantRowAndColumn.getX());
                    int distanceY = Math.abs(tile.getColumn() - plantRowAndColumn.getY());
                    if ((distanceX <= range) && (distanceY <= range)) {
                        for (Zombie zombie : tile.getAliveZombies()) {
                            //todo: damage on zombies
                        }
                    }
                }
            }
        }

        if (tags.contains("sun")) {
            int numberOfSun = plant.getPlantStats().getAttributes().get("sun_quantity");
            Sun sun = new Sun(numberOfSun, plant.getPosition());

            //todo: increase sun amount of user
        }
    }
}
