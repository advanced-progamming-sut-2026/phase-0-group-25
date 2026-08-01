package src.Model.PlantsAndZombies.Projectiles;

import src.Menu.GamePlayMenu;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;

public class Dynamite {
    private double velocity = 0.5;
    private Position position;
    private double damage = 10;

    private static GamePlay GAME = GamePlayMenu.getGamePlay();

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