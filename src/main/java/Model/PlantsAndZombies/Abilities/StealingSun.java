package Model.PlantsAndZombies.Abilities;

import Model.PlantsAndZombies.*;
import Model.Sun.Sun;
import Model.Tile;


public class StealingSun implements Ability {
    private double stolenSun = 0;
    private boolean isActivate = false;

    @Override
    public void executeAbility(Entity entity) {
        Zombie zombie = (Zombie) entity;

        if (zombie.getZombieStats().getName().equals("Turquoise")) {
            Position zombieRowAndColumn = Position.getRowAndColumn(zombie.getPosition());
            int zombieColumn = (int) zombieRowAndColumn.getX();
            int zombieRow = (int) zombieRowAndColumn.getY();

            if (!this.isActivate) {

                for (int i = 0; i <= 4; i++) {
                    //todo: proper getter tile function with defined row and column
                    Tile tile = game.getTile();
                    //todo: proper getter for plants in defined tile;
                    if (tile.getPlants() == null) {
                        continue;
                    } else {
                        zombie.setCurrentVelocity(0);
                        //todo: define a function for getting current time
                        zombie.setLastActionTime(game.getCurrentTime());
                        this.setActivate(true);
                    }
                }
            } else {
                //todo: define a function for getting current time
                if ((game.getCurrentTime() - zombie.getLastActionTime()) < 5) {
                    //todo: define a function which gives current sun amount;
                    if (game.getSunAmount() < 2.5) {
                        this.stolenSun += game.getSunAmount();
                    } else {
                        this.stolenSun += 2.5;
                    }
                } else {
                    for (int i = 0; i <= 4; i++) {
                        //todo: proper getter tile function with defined row and column
                        Tile tile = game.getTile();
                        //todo: proper getter for plants in defined tile;
                        for (BattlePlant plant : tile.getPlants()) {
                            plant.setAlive(false);
                        }
                    }
                    this.setActivate(false);
                }
            }

        } else if (zombie.getZombieStats().getName().equals("Ra")) {
            //todo: a function which attracts all untouchable suns on game board;
            for (Sun sun : game.getOnBoardSuns()) {
                this.stolenSun += sun.getNumberOfSun();
            }
        }
    }

    public double getStolenSun() {
        return this.stolenSun;
    }

    public void setActivate(boolean activate) {
        isActivate = activate;
    }
}
