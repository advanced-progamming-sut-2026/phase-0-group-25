package src.Enums;

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

    ZombieType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ZombieType fromName(String name) {
        for (ZombieType type : values()) {
            if (type.name.equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown zombie name: " + name);
    }
}
