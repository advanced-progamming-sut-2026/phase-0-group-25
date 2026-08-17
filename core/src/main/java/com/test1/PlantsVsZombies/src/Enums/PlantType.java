package com.test1.PlantsVsZombies.src.Enums;

public enum PlantType {
    SUNFLOWER("SUNFLOWER", "IMAGE_UI_SUNFLOWER", "768/INITIAL/PLANT/SUNFLOWER/SUNFLOWER.PAM"),
    TWIN_SUNFLOWER("TWIN_SUNFLOWER"),
    SUN_SHROOM("SUN_SHROOM"),
    PRIMAL_SUNFLOWER("PRIMAL_SUNFLOWER"),
    GOLD_BLOOM("GOLD_BLOOM"),
    PEASHOOTER("PEASHOOTER"),
    REPEATER("REPEATER"),
    THREEPEATER("THREEPEATER"),
    SNOW_PEA("SNOW_PEA"),
    ROTOBAGA("ROTOBAGA"),
    PEA_POD("PEA_POD"),
    SPLIT_PEA("SPLIT_PEA"),
    CITRON("CITRON"),
    CAULIPOWER("CAULIPOWER"),
    ELECTRIC_BLUEBERRY("ELECTRIC_BLUEBERRY"),
    BOWLING_BULB("BOWLING_BULB"),
    CACTUS("CACTUS"),
    FIRE_PEASHOOTER("FIRE_PEASHOOTER"),
    STARFRUIT("STARFRUIT"),
    GOO_PEASHOOTER("GOO_PEASHOOTER"),
    MEGA_GATLING_PEA("MEGA_GATLING_PEA"),
    SEA_SHROOM("SEA_SHROOM"),
    PUFF_SHROOM("PUFF_SHROOM"),
    FUME_SHROOM("FUME_SHROOM"),
    CABBAGE_PULT("CABBAGE_PULT"),
    KERNEL_PULT("KERNEL_PULT"),
    MELON_PULT("MELON_PULT"),
    WINTER_MELON("WINTER_MELON"),
    PEPPER_PULT("PEPPER_PULT"),
    POTATO_MINE("POTATO_MINE"),
    PRIMAL_POTATO_MINE("PRIMAL_POTATO_MINE"),
    CHERRY_BOMB("CHERRY_BOMB"),
    SQUASH("SQUASH"),
    GRAPESHOT("GRAPESHOT"),
    JALAPENO("JALAPENO"),
    DOOM_SHROOM("DOOM_SHROOM"),
    TANGLE_KELP("TANGLE_KELP"),
    ICEBERG_LETTUCE("ICEBERG_LETTUCE"),
    BONK_CHOY("BONK_CHOY"),
    PHAT_BEET("PHAT_BEET"),
    CHOMPER("CHOMPER"),
    WASABI_WHIP("WASABI_WHIP"),
    KIWIBEAST("KIWIBEAST"),
    WALL_NUT("WALL_NUT"),
    TALL_NUT("TALL_NUT"),
    ENDURIAN("ENDURIAN"),
    GARLIC("GARLIC"),
    SWEET_POTATO("SWEET_POTATO"),
    EXPLODE_O_NUT("EXPLODE_O_NUT"),
    PUMPKIN("PUMPKIN"),
    SUN_BEAN("SUN_BEAN"),
    TORCHWOOD("TORCHWOOD"),
    MAGNET_SHROOM("MAGNET_SHROOM"),
    HYPNO_SHROOM("HYPNO_SHROOM"),
    CAT_TAIL("CAT_TAIL"),
    IMITATER("IMITATER"),
    ICE_SHROOM("ICE_SHROOM"),
    LILY_PAD("LILY_PAD"),
    HOT_POTATO("HOT_POTATO"),
    GRAVE_BUSTER("GRAVE_BUSTER"),
    ENLIGHTEN_MINT("ENLIGHTEN_MINT"),
    APPEASE_MINT("APPEASE_MINT"),
    ARMA_MINT("ARMA_MINT"),
    BOMBARD_MINT("BOMBARD_MINT"),
    ENFORCE_MINT("ENFORCE_MINT"),
    REINFORCE_MINT("REINFORCE_MINT"),
    ENCHANT_MINT("ENCHANT_MINT"),
    PIERCE_MINT("PIERCE_MINT"),
    CATTAIL_MINT("CATTAIL_MINT"),
    MARIGOLD("MARIGOLD");

    private final String name;
    private final String iconAssetId;
    private final String idleAnimationPath;

    /**
     * Default constructor: derives the collection-screen icon asset id and
     * the idle-animation PAM path from the plant's name using a consistent
     * convention. If your real TextureBank keys or PAM paths for a specific
     * plant don't follow this pattern, use the 3-arg constructor on that
     * one entry to override it explicitly.
     */
    PlantType(String name) {
        this(name, "IMAGE_REWARD_ICON_" + name, "768/INITIAL/PLANT/" + name + "/" + name + ".PAM");
    }

    PlantType(String name, String iconAssetId, String idleAnimationPath) {
        this.name = name;
        this.iconAssetId = iconAssetId;
        this.idleAnimationPath = idleAnimationPath;
    }

    public static PlantType fromName(String name) {
        for (PlantType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    /**
     * Asset id for the thumbnail shown in the Collection screen grid.
     * See constructor javadoc re: placeholder convention.
     */
    public String getIconAssetId() {
        return iconAssetId;
    }

    /**
     * PAM animation path for the idle animation shown in the plant's
     * detail box on the Collection screen.
     */
    public String getIdleAnimationPath() {
        return idleAnimationPath;
    }
}
