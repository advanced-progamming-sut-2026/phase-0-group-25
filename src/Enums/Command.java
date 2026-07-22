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
    ChangeDifficulty("menu settings change-difficulty -l (\\S+)"),
    RegisterAccount("register -u (.+?) -p (.+?) (.+?) -n (.+?) -e (.+?) -g (.+?)"),
    PickQuestion("pick question -q (.+?) -a (.+?) -c (.+?)"),
    LoginAccount("login -u (\\S+) -p (\\S+)( -stay-logged-in)?"),
    ForgetPassword("forget password -u (\\S+) -e (\\S+) answer -a (.+?)"),
    SetNewPassword("set password -p (\\S+)"),
    MenuLogout("menu logout"),
    ShowUnreadNews("menu news show-unread"),
    ShowAllNews("menu news show-all"),
    ChangeUsername("menu profile change-username -u (.+?)"),
    ChangeNickname("menu profile change-nickname -u (.+?)"),
    ChangeEmail("menu profile change-email -e (.+?)"),
    ChangePassword("menu profile change-password -p (\\S+) -o (\\S+)"),
    ShowProfileInfo("menu profile show-info"),
    AdvanceTime("^advance time -t\\s+(?<count>\\d+)\\s+ticks$"),
    CollectSun("^collect sun -l \\s*\\((?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\)$"),
    ShowSunAmount("show sun amount"),
    ShowPlantFoodAmount("show sun amount"),
    CheatAddSuns("cheat add -n <count> suns"),
    CheatCooldown("cheat remove-cooldown"),
    CheatPlantFood("cheat add-plant-food"),
    ReleaseTheNuke("release the nuke"),
    PlantPlant("^plant plant -t\\s+(?<type>[\\w-]+)\\s+-l\\s*\\((?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\)$"),
    PluckPlant("^pluck plant -l \\s*\\((?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\)$"),
    FeedPlant("^feed plant -l \\s*\\((?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\)$"),
    ShowMap("show map"),
    ShowPlantsStatus("show plants status"),
    ShowTileStatus("^show tile status -l \\s*\\((?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\)$");

    private final String regex;
    Command(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}