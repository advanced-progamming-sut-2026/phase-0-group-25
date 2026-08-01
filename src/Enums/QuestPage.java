package src.Enums;

public enum QuestPage {
    ZOMBIE_SLAYER("zombie"),
    GARDENER("gardener"),
    SUN_COLLECTOR("sun"),
    CHALLENGES("challenges");

    private final String commandName;

    QuestPage(String commandName) {
        this.commandName = commandName;
    }

    public static QuestPage fromCommandName(String name) {
        for (QuestPage p : values()) {
            if (p.commandName.equalsIgnoreCase(name)) return p;
        }
        return null;
    }

    public String getCommandName() {
        return commandName;
    }
}