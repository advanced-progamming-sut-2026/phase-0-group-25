package com.test1.PlantsVsZombies.src.Enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum ZombieType {
    DEFAULT("DEFAULT", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM", "idle"),
    CONE_HEAD("CONE_HEAD", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_ARMOR1", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM", "idle", new ArrayList<>(List.of("zombie_armor_cone_norm", "zombie_tutorial_80x83_2|IMAGE_ZOMBIE_ZOMBIE_TUTORIAL_ZOMBIE_TUTORIAL_80X83_2"))),
    BUCKET_HEAD("BUCKET_HEAD", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_ARMOR2", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM", "idle", new ArrayList<>(List.of("zombie_armor_bucket_norm", "zombie_tutorial_96x97|IMAGE_ZOMBIE_ZOMBIE_TUTORIAL_ZOMBIE_TUTORIAL_96X97"))),
    BRICK_HEAD("BRICK_HEAD", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_ARMOR4", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL/ZOMBIE_TUTORIAL.PAM", "idle", new ArrayList<>(List.of("zombie_armor_brick_norm", "brick_undamaged", "trowel_base_wCement"))),
    KNIGHT("KNIGHT", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_ARMOR3", "768/FULL/ZOMBIE/ZOMBIE_DARK_BASIC/ZOMBIE_DARK_BASIC.PAM", "idle", new ArrayList<>(List.of("_zombie_armor_crown_states", "zombie_armor_crown_damage_02", "zombie_armor_crown_damage_01", "zombie_armor_crown_norm"))),
    GARGANTUAR("GARGANTUAR", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_FUTURE_GARGANTUAR", "768/FULL/ZOMBIE/GARGANTUAR/GARGANTUAR.PAM", "idle"),
    IMP("IMP", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TUTORIAL_IMP", "768/INITIAL/ZOMBIE/ZOMBIE_TUTORIAL_IMP/ZOMBIE_TUTORIAL_IMP.PAM", "idle"),
    RA("RA", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_RA", "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_RA/ZOMBIE_EGYPT_RA.PAM", "idle"),
    EXPLORER("EXPLORER", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_EXPLORER", "768/INITIAL/ZOMBIE/ZOMBIE_EXPLORER/ZOMBIE_EXPLORER.PAM", "idle"),
    TOMB_RAISER("TOMB_RAISER", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_TOMB_RAISER", "768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_TOMBRAISER/ZOMBIE_EGYPT_TOMBRAISER.PAM", "idle"),
    DODO("DODO", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_DODO", "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_DODORIDER/ZOMBIE_ICEAGE_DODORIDER.PAM", "idle"),
    HUNTER("HUNTER", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_HUNTER", "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_HUNTER/ZOMBIE_ICEAGE_HUNTER.PAM", "idle"),
    TROGLOBITE("TROGLOBITE", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_ICEAGE_TROGLOBITE", "768/FULL/ZOMBIE/ZOMBIE_ICEAGE_TROGLOBITE/ZOMBIE_ICEAGE_TROGLOBITE.PAM", "idle"),
    FISHERMAN("FISHERMAN", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_FISHERMAN", "768/FULL/ZOMBIE/ZOMBIE_BEACH_FISHERMAN/ZOMBIE_BEACH_FISHERMAN.PAM", "idle"),
    OCTOPUS("OCTOPUS", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_OCTOPUS", "768/FULL/ZOMBIE/ZOMBIE_BEACH_OCTOPUS/ZOMBIE_BEACH_OCTOPUS.PAM", "idle"),
    SNORKEL("SNORKEL", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_BEACH_SNORKEL", "768/FULL/ZOMBIE/ZOMBIE_BEACH_SNORKELER/ZOMBIE_BEACH_SNORKELER.PAM", "idle"),
    JUGGLER("JUGGLER", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_JUGGLER", "768/FULL/ZOMBIE/ZOMBIE_DARK_JESTER/ZOMBIE_DARK_JESTER.PAM", "idle"),
    WIZARD("WIZARD", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_WIZARD", "768/FULL/ZOMBIE/ZOMBIE_DARK_WIZARD/ZOMBIE_DARK_WIZARD.PAM", "idle"),
    KING("KING", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_KING", "768/FULL/ZOMBIE/ZOMBIE_DARK_KING/ZOMBIE_DARK_KING.PAM", "idle"),
    IMP_DRAGON("IMP_DRAGON", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_DARK_IMP_DRAGON", "768/FULL/ZOMBIE/ZOMBIE_DARK_IMP_DRAGON/ZOMBIE_DARK_IMP_DRAGON.PAM", "idle"),
    ALL_STAR("ALL_STAR", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_MODERN_ALLSTAR", "768/FULL/ZOMBIE/ZOMBIE_MODERN_ALLSTAR/ZOMBIE_MODERN_ALLSTAR.PAM", "idle"),
    ARCADE("ARCADE", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_EIGHTIES_ARCADE", "768/FULL/ZOMBIE/ZOMBIE_80S_ARCADE/ZOMBIE_80S_ARCADE.PAM", "idle"),
    UMBRELLA("UMBRELLA", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_LOSTCITY_JANE", "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_JANE/ZOMBIE_LOSTCITY_JANE.PAM", "idle"),
    TURQUOISE("TURQUOISE", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_LOSTCITY_CRYSTALSKULL", "768/FULL/ZOMBIE/ZOMBIE_LOSTCITY_CRYSTALSKULL/ZOMBIE_LOSTCITY_CRYSTALSKULL.PAM", "idle"),
    PROSPECTOR("PROSPECTOR", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_PROSPECTOR", "768/FULL/ZOMBIE/ZOMBIE_PROSPECTOR/ZOMBIE_PROSPECTOR.PAM", "idle"),
    PIANO("PIANO", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_PIANO", "768/FULL/ZOMBIE/ZOMBIE_PIANO/ZOMBIE_PIANO.PAM", "idle"),
    NEWSPAPER("NEWSPAPER", "IMAGE_UI_ALMANAC_PACKETS_ZOMBIES_MODERN_NEWSPAPER", "768/FULL/ZOMBIE/ZOMBIE_MODERN_NEWSPAPER/ZOMBIE_MODERN_NEWSPAPER.PAM", "idle_newspaper");

    private final String name;
    private final String iconAssetId;
    private final String idleAnimationPath;
    private final String stateName;
    private final HashMap<String, Boolean> visibility;

    /**
     * Default constructor: derives the collection-screen icon asset id and
     * the idle-animation PAM path from the zombie's name using the same
     * convention shown in your PamPlayer sample
     * ("768/INITIAL/ZOMBIE/ZOMBIE_EGYPT_BASIC/ZOMBIE_EGYPT_BASIC.PAM").
     * Use the 3-arg constructor on individual entries to override.
     */


    ZombieType(String name, String iconAssetId, String idleAnimationPath, String stateName) {
        this.name = name;
        this.iconAssetId = iconAssetId;
        this.idleAnimationPath = idleAnimationPath;
        this.stateName = stateName;
        this.visibility = new HashMap<>();
    }
    ZombieType(String name, String iconAssetId, String idleAnimationPath, String stateName, ArrayList<String> visibleParts) {
        this.name = name;
        this.iconAssetId = iconAssetId;
        this.idleAnimationPath = idleAnimationPath;
        this.stateName = stateName;
        this.visibility = new HashMap<>();
        for (String visiblePart: visibleParts)
            visibility.put(visiblePart, true);
    }

    public HashMap<String, Boolean> getVisibility() {
        return visibility;
    }

    public String getStateName() {
        return stateName;
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
