package com.test1.PlantsVsZombies.src.Enums;

public enum PlantCategory {
    SUN_PRODUCER("Sun Producer"),
    SHOOTER("Shooter"),
    LOBBER("Lobber"),
    EXPLOSIVE("Explosive"),
    MELEE_ATTACKER("Melee"),
    WALL_NUT("Wall-nut"),
    MODIFIER("Modifier"),
    STRIKE_THROUGH("Strike-through"),
    HOMING("Homing"),
    MINT("Mint"),
    MARIGOLD("Marigold");

    private final String string;

    public String getString() {
        return string;
    }

    PlantCategory(String string) {
        this.string = string;
    }

    public static PlantCategory findCategoryByString(String string){
        for (PlantCategory plantCategory: PlantCategory.values())
            if(plantCategory.getString().equals(string))
                return plantCategory;
        return null;
    }
}
