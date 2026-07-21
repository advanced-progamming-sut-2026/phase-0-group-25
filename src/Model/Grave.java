package src.Model;

import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Entity;
import src.Model.PlantsAndZombies.GameDataLoader;

public class Grave extends Entity {
    private Tile thisTile;

    public Grave(Tile thisTile) {
        this.thisTile = thisTile;
    }

    @Override
    public void update(GamePlay thisGame) {
        if(this.currentHP <= 0) {
            thisTile.setGrave(null);
            thisGame
        }
    }
}
