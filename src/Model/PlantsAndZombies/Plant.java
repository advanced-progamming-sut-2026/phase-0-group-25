package src.Model.PlantsAndZombies;

import src.Enums.PlantCategory;

public abstract class Plant extends Entity {
    protected String name;
    protected PlantCategory category;
    private boolean hasBoost;




    public abstract void update();


}
