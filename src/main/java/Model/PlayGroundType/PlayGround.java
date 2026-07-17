package Model.PlayGroundType;
import Model.Mower;
import Model.Tile;

import java.util.ArrayList;

public abstract class PlayGround {
    private ArrayList<Mower> mowers;
    private ArrayList<Tile> tiles;

    public abstract void makeGround();
}
