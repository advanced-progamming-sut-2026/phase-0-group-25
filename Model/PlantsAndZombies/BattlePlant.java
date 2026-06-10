package Model.PlantsAndZombies;

import Enums.Tag;
import Model.PlantsAndZombies.Abilities.Ability;

import java.util.*;

public class BattlePlant extends Plant {
    private ArrayList<Tag> tags;
    private double lastActionTime;
    private PlantStats plantStats;
    protected ArrayList<Ability> abilities;

    @Override
    public void update() {

    }
}
