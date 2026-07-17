package Model;

import Model.PlantsAndZombies.Plant;
import Model.PlantsAndZombies.Position;

public class Tile {
    private Plant plant;
    private Position position;
    private boolean isArable;
    // TODO : adding Grave...
    private String kindOfTile;
    //TODO : arraylist of plants and zombies projectile...

    public Tile(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public void setPlant(Plant plant) {
        if (isArable)
            this.plant = plant;
    }

    public void Action() {}
}
