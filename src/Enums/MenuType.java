package src.Enums;

public enum MenuType {
    Login("login"),
    Signup("signup"),
    Game("game"),
    ChoosePlant("choose plant"),
    CoinWallet("coin wallet"),
    Collection("collection"),
    GamePlay("game play"),
    GemWallet("gem wallet"),
    GreenHouse("greenhouse"),
    LeaderBoard("leader board"),
    Main("main"),
    News("news"),
    Profile("profile"),
    Quest("quest"),
    Setting("settings"),
    Shop("shop"),
    Network("network"),
    TravelLog("travel log");

    private final String string;
    MenuType(String string) {
        this.string = string;
    }

    public String getString() {
        return string;
    }
}
