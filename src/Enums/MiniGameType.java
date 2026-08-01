package src.Enums;

public enum MiniGameType {
    VASEBREAKER("vasebreaker"),
    WALNUT_BOWLING("walnut bowling"),
    I_ZOMBIE("i zombie");

    private final String displayName;

    MiniGameType(String displayName) {
        this.displayName = displayName;
    }

    public static MiniGameType fromDisplayName(String name) {
        for (MiniGameType type : values()) {
            if (type.displayName.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }

    public String getDisplayName() {
        return displayName;
    }
}