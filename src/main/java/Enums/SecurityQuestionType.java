package Enums;

public enum SecurityQuestionType {
    FAVORITE_COLOR(1, "What is your favorite color?"),
    FIRST_PET(2, "What was the name of your first pet?"),
    BORN_CITY(3, "What city were you born in?");

    private final int id;
    private final String description;

    SecurityQuestionType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public static SecurityQuestionType getById(int id) {
        for (SecurityQuestionType type : values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        return null;
    }
}
