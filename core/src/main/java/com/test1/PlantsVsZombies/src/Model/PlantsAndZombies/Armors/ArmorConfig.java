package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors;

public enum ArmorConfig {
    CONE("Cone", 370, false),
    BUCKET("Bucket", 1100, true),
    BRICK("Brick", 2200, false),
    SHOULDER_ARMOR("Shoulder Armor", 1600, true),
    CROWN("Crown", 1600, true),
    NEWSPAPER("Cewspaper", 800, false),
    ARCADE("Arcade", 1100, false);

    private final String type;
    private final int baseHP;
    private final boolean isMetallic;

    ArmorConfig(String type, int baseHP, boolean isMetallic) {
        this.type = type;
        this.baseHP = baseHP;
        this.isMetallic = isMetallic;
    }

    public Armor createArmor() {
        return new Armor(this.type, this.baseHP, this.isMetallic);
    }
}
