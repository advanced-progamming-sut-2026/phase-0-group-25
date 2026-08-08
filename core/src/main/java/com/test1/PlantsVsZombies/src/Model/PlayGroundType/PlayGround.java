package com.test1.PlantsVsZombies.src.Model.PlayGroundType;

import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.Tile;

import java.util.ArrayList;

public abstract class PlayGround {
    private ArrayList<Mower> mowers;
    private ArrayList<Tile> tiles;

    public abstract void makeGround();
}
