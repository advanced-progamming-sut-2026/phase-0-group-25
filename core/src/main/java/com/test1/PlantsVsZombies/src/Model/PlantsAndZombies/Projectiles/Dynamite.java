package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class Dynamite {
    private final double velocity = 200;
    private Position position;
    private final double damage = 30;

    private final GamePlay GAME = GamePlay.activeInstance;

    public Dynamite(Position position) {
        this.position = position;
    }


    public void update() {
        double finalXPosition = (this.velocity * 0.1) + position.getX();

        this.position = new Position(finalXPosition, this.position.getY());

        checkCollision();

    }

    public void checkCollision() {
        for (BattlePlant plant : GAME.getGamePlants()) {
            if (plant.getPosition().equals(this.position)) {
                plant.setCurrentHP(plant.getCurrentHP() - damage);
            }
        }
    }

    public Position getPosition() {
        return position;
    }
}
