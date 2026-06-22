package src.Enums;

public enum Command {
    changeMenu("menu enter (.+?)"),
    ShowMenu("menu show current"),
    Exit("menu exit");

    private final String regex;
    Command(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
