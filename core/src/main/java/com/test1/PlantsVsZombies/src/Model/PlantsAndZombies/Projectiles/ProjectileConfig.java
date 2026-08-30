package com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles;

import com.test1.PlantsVsZombies.src.Enums.PlantType;

public enum ProjectileConfig {
    PEA("pea",
        "768/INITIAL/EFFECTS/T_PEA_PROJECTILE/T_PEA_PROJECTILE.PAM", "animation"),
    MEGA_PEA("mega pea",
        "768/FULL/EFFECTS/PEAPOD_PLANTFOOD_GIANTPEA/PEAPOD_PLANTFOOD_GIANTPEA.PAM", "animation"),
    ICY_PEA("icy_pea",
        "768/INITIAL/EFFECTS/T_SNOW_PEA/T_SNOW_PEA.PAM", "animation"),
    FIRING_PEA("firing_pea",
        "768/INITIAL/EFFECTS/T_FIRE_PEA/T_FIRE_PEA.PAM", "animation"),
    BLUE_FIRING_PEA("blue_firing_pea",
        "768/INITIAL/EFFECTS/T_FIRE_PEA_BLUE/T_FIRE_PEA_BLUE.PAM", "animation"),
    ROTOBAGA("rotobaga",
        "768/FULL/EFFECTS/ROTORUTABAGA_PROJECTILE2/ROTORUTABAGA_PROJECTILE2.PAM", "animation"),
    CITRON("citron",
        "768/FULL/EFFECTS/CITRON_CITRUS_ORB/CITRON_CITRUS_ORB.PAM", "Citron_Citrus_Orb"),
    STARFRUIT("starfruit",
        "768/INITIAL/EFFECTS/STARFRUIT_PROJECTILE_PLANTFOOD/STARFRUIT_PROJECTILE_PLANTFOOD.PAM", "animation"),
    GOO("goo",
        "768/INITIAL/EFFECTS/GOOPEASHOOTER_PROJECTILES/GOOPEASHOOTER_PROJECTILES.PAM", "projectile_t1"),
    SEA_SHROOM("sea_shroom",
        "768/FULL/EFFECTS/SEASHOOTER_PROJECTILE/SEASHOOTER_PROJECTILE.PAM", "animation"),
    PUFF_SHROOM("puff_shroom",
        "768/INITIAL/EFFECTS/T_PUFFSHROOM_PROJECTILE/T_PUFFSHROOM_PROJECTILE.PAM", "animation"),
    CACTUS("cactus",
        "768/INITIAL/EFFECTS/CACTUS_PROJECTILE/CACTUS_PROJECTILE.PAM", "idle"),
    FUME_SHROOM("fume_shroom",
        "768/INITIAL/EFFECTS/FUMESHROOM_BUBBLES/FUMESHROOM_BUBBLES.PAM", "special"),
    GRAPESHOT("grapeshot",
        "768/INITIAL/EFFECTS/GRAPESHOT_PROJECTILE/GRAPESHOT_PROJECTILE.PAM", "animation_forward"),
    CABBAGE("cabbage",
        "768/INITIAL/EFFECTS/T_CABBAGEPULT_PROJECTILE/T_CABBAGEPULT_PROJECTILE.PAM", "animation"),
    CORN("corn",
        "768/INITIAL/EFFECTS/T_KERNALPULT_PROJECTILE/T_KERNALPULT_PROJECTILE.PAM", "animation"),
    BUTTER("butter",
        "768/FULL/EFFECTS/MANGOFIER_PROJECTILE/MANGOFIER_PROJECTILE.PAM", "mangofier_projectile2"),
    MELON("melon",
        "768/INITIAL/EFFECTS/T_MELON_PROJECTILE/T_MELON_PROJECTILE.PAM", "animation"),
    WINTER_MELON("winter_melon",
        "768/FULL/EFFECTS/T_WINTERMELON_PROJECTILE/T_WINTERMELON_PROJECTILE.PAM", "animation"),
    PEPPER("pepper",
        "768/FULL/EFFECTS/PEPPERPULT_PROJECTILE/PEPPERPULT_PROJECTILE.PAM", "animation"),

    THISTLE("thistle",
        "768/INITIAL/EFFECTS/HOMING_THISTLE_PROJECTILE/HOMING_THISTLE_PROJECTILE.PAM", "animation"),
    CAULIPOWER("caulipower",
        "768/INITIAL/EFFECTS/CAULIPOWER_PROJECTILE/CAULIPOWER_PROJECTILE.PAM", "animation"),
    ELECTRIC_BLUEBERRY("electric blueberry",
        "768/INITIAL/EFFECTS/ELECTRICBLUEBERRY_CLOUD_PROJECTILE/ELECTRICBLUEBERRY_CLOUD_PROJECTILE.PAM", "idle"),

    FIRE("fire",
        "768/FULL/EFFECTS/ZOMBOSS_DARK_FIREBALL/ZOMBOSS_DARK_FIREBALL.PAM", "fall"),
    MISSILE("missile",
        "768/FULL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_DARK/ZOMBOSS_MISSILE_EXPLOSION_DARK.PAM", "missile"),
    ICE("ice",
        "768/FULL/EFFECTS/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE/ZOMBOSS_MISSILE_EXPLOSION_ICEAGE.PAM", "missile"),
    SHARK("shark",
        "768/FULL/EFFECTS/ZOMBOSS_SHARK_PROJECTILE/ZOMBOSS_SHARK_PROJECTILE.PAM", "walk");

    private final String type;
    private final String animation;
    private final String clip;

    ProjectileConfig(String type, String animation, String clip) {
        this.type = type;
        this.animation = animation;
        this.clip = clip;
    }

    public static ProjectileConfig fromName(String type) {
        for (ProjectileConfig projectile : values()) {
            if (projectile.type.equalsIgnoreCase(type)) {
                return projectile;
            }
        }
        return null;
    }

    public String getAnimation() {
        return animation;
    }

    public String getClip() {
        return clip;
    }
}
