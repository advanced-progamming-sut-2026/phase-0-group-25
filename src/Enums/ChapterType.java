package src.Enums;

public enum ChapterType {
    ANCIENT_EGYPT("ancient egypt"),
    DARK_AGE("dark age"),
    FROSTBITE_CAVES("frostbite caves"),
    BIG_WAVE_BEACH("big wave beach");

    private final String name;

    ChapterType(String name) {
        this.name = name;
    }

    public static ChapterType getByName(String name) {
        for (ChapterType chapterType : values()) {
            if (chapterType.name.equalsIgnoreCase(name)) {
                return chapterType;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
}
