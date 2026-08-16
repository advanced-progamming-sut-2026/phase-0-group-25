package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Menu.GamePlayMenu;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public class Dynamite {
    private double velocity = 0.5;
    private Position position;
    private double damage = 10;

    private GamePlay GAME = GamePlay.activeInstance;

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
}
