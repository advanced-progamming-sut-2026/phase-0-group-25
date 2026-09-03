package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Armors;

import java.util.List;

public enum ArmorConfig {
    CONE("Cone", 370, false,
        List.of("zombie_armor_cone_norm",
            "zombie_armor_cone_damage_01",
            "zombie_armor_cone_damage_02")),
    BUCKET("Bucket", 1100, true,
        List.of("zombie_armor_bucket_norm",
            "zombie_armor_bucket_damage_01",
            "zombie_armor_bucket_damage_02")),
    BRICK("Brick", 2200, false,
        List.of("zombie_armor_brick_norm",
            "zombie_armor_brick_damage_01",
            "zombie_armor_brick_damage_02")),
    SHOULDER_ARMOR("Shoulder Armor", 1600, true,
        List.of("zombie_shoulder_armor_norm",
            "zombie_shoulder_armor_damage_01",
            "zombie_shoulder_armor_damage_02")),
    CROWN("Crown", 1600, true,
        List.of("zombie_armor_crown_norm",
            "zombie_armor_crown_damage_01",
            "zombie_armor_crown_damage_02")),
    NEWSPAPER("Newspaper", 800, false,
        List.of("_zombie_newspaper",
            "_zombie_newspaper_dmg1",
            "_zombie_newspaper_dmg2")),
    ARCADE("Arcade", 1100, false,
        List.of("zombie_armor_crown_norm",
            "zombie_armor_crown_damage_01",
            "zombie_armor_crown_damage_02"));

    private final String type;
    private final int baseHP;
    private final boolean isMetallic;
    private final List<String> animations;

    ArmorConfig(String type, int baseHP, boolean isMetallic, List<String> animations) {
        this.type = type;
        this.baseHP = baseHP;
        this.isMetallic = isMetallic;
        this.animations = animations;
    }

    public Armor createArmor() {
        return new Armor(this.type, this.baseHP, this.isMetallic, this.animations);
    }

    public String getType() {
        return type;
    }
}
