package Model.PlantsAndZombies;

import Enums.Tag;

import java.util.ArrayList;

public class BattlePlant extends Plant {
    private ArrayList<Tag> tags;
    private double lastActionTime;
    private PlantStats plantStats;
    protected ArrayList<Ability> abilities;

    @Override
    public void update() {

    }
}
