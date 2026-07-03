package src.Enums;

public enum Command {
    changeMenu("menu enter (.+?)"),
    ShowMenu("menu show current"),
    Exit("menu exit"),
    RegisterAccount("register -u (.+?) -p (.+?) (.+?) -n (.+?) -e (.+?) -g (.+?)"),
    PickQuestion("pick question -q (.+?) -a (.+?) -c (.+?)");

    private final String regex;
    Command(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
