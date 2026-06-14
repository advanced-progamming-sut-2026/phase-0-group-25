package src.Model.PlayGroundType;
import src.Model.Mower;
import src.Model.PlantsAndZombies.Tile;

import java.util.ArrayList;

public abstract class PlayGround {
    private ArrayList<Mower> mowers;
    private ArrayList<Tile> tiles;

    public abstract void makeGround();
}
