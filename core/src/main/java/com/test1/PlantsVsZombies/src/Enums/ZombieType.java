package com.test1.PlantsVsZombies.src.Enums;

public enum ZombieType {
    DEFAULT("DEFAULT"),
    CONE_HEAD("CONE_HEAD"),
    BUCKET_HEAD("BUCKET_HEAD"),
    BRICK_HEAD("BRICK_HEAD"),
    KNIGHT("KNIGHT"),
    GARGANTUAR("GARGANTUAR"),
    IMP("IMP"),
    RA("RA"),
    EXPLORER("EXPLORER"),
    TOMB_RAISER("TOMB_RAISER"),
    DODO("DODO"),
    HUNTER("HUNTER"),
    TROGLOBITE("TROGLOBITE"),
    FISHERMAN("FISHERMAN"),
    OCTOPUS("OCTOPUS"),
    SNORKEL("SNORKEL"),
    JUGGLER("JUGGLER"),
    WIZARD("WIZARD"),
    KING("KING"),
    IMP_DRAGON("IMP_DRAGON"),
    ALL_STAR("ALL_STAR"),
    ARCADE("ARCADE"),
    UMBRELLA("UMBRELLA"),
    TURQUOISE("TURQUOISE"),
    PROSPECTOR("PROSPECTOR"),
    PIANO("PIANO"),
    NEWSPAPER("NEWSPAPER");

    private final String name;
    private final String iconAssetId;
    private final String idleAnimationPath;

    /**
     * Default constructor: derives the collection-screen icon asset id and
     * the idle-animation PAM path from the zombie's name using the same
     * convention shown in your PamPlayer sample
     * ("768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_BASIC/ZOMBIE_EGYPT_BASIC.PAM").
     * Use the 3-arg constructor on individual entries to override.
     */
    ZombieType(String name) {
        this(name, "IMAGE_REWARD_ICON_" + name, "768/INITIAL/ZOMBIE/" + name + "/" + name + ".PAM");
    }

    ZombieType(String name, String iconAssetId, String idleAnimationPath) {
        this.name = name;
        this.iconAssetId = iconAssetId;
        this.idleAnimationPath = idleAnimationPath;
    }

    public static ZombieType fromName(String name) {
        for (ZombieType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown zombie name: " + name);
    }

    public String getName() {
        return name;
    }

    public String getIconAssetId() {
        return iconAssetId;
    }

    public String getIdleAnimationPath() {
        return idleAnimationPath;
    }
}
