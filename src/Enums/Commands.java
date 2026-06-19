package src.Enums;

public enum Commands {
    changeMenu("menu enter (.+?)");

    private final String regex;
    Commands(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
