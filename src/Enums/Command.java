package src.Enums;

public enum Command {
    changeMenu("menu enter (?!chapter -c )(.+?)"),
    ShowMenu("menu show current"),
    Exit("menu exit"),
    EnterChapter("menu enter chapter -c (.+?)"),
    EnterGreenHouse("menu greenhouse"),
    EnterTravelLog("menu travel-log"),
    EnterLeaderBoard("menu leaderboard"),
    EnterCoinWallet("menu coin-wallet"),
    EnterGemWallet("menu gem-wallet"),
    Cheat("menu cheat add (\\d+) (coin|diamond)"),
    RegisterAccount("register -u (.+?) -p (.+?) (.+?) -n (.+?) -e (.+?) -g (.+?)"),
    PickQuestion("pick question -q (.+?) -a (.+?) -c (.+?)"),
    LoginAccount("login -u (\\S+) -p (\\S+)( -stay-logged-in)?"),
    ForgetPassword("forget password -u (\\S+) -e (\\S+) answer -a (.+?)"),
    SetNewPassword("set password -p (\\S+)"),
    MenuLogout("menu logout");

    private final String regex;
    Command(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}
