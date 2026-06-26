package src.Model;

import src.Model.PlantsAndZombies.Plant;
import src.Model.PlantsAndZombies.Position;

public class Tile {
    private Plant plant;
    private Position position;
    private boolean isArable;
    private String kindOfTile;

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
