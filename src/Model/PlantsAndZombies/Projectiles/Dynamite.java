package src.Model.PlantsAndZombies.Projectiles;

import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;

public class Dynamite {
    private double velocity = 0.5;
    private Position position;
    private double damage = 10;

    public Dynamite(Position position) {
        this.position = position;
    }


    public void update() {
        Position dynamiteRowAndColumn = Position.getRowAndColumn(position);
        double finalXPosition = (this.velocity * 0.1) + position.getX();

        this.position = new Position(finalXPosition, this.position.getY());

        checkCollision();

    }

    public void checkCollision() {
        //todo: getter for active plants on game board
        for (BattlePlant plant : game.getPlants()) {
            if (plant.getPosition().equals(this.position)) {
                plant.setCurrentHP(plant.getCurrentHP() - damage);
            }
        }
    }
}