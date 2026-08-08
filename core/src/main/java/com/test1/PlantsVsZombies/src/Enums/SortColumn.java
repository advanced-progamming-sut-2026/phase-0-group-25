package com.test1.PlantsVsZombies.src.Enums;

public enum SortColumn {
    CHAPTER("chapter"),
    MINIGAMES("minigames"),
    DAILY("daily"),
    NONDAILY("nondaily");

    private final String commandName;

    SortColumn(String commandName) {
        this.commandName = commandName;
    }

    public static SortColumn fromCommandName(String name) {
        for (SortColumn col : values()) {
            if (col.commandName.equalsIgnoreCase(name)) {
                return col;
            }
        }
        return null;
    }

    public String getCommandName() {
        return commandName;
    }
}
