package src.View.ConcreteViews;

import src.View.ViewInterfaces.ProfileMenuView;

public class ProfileMenuTerminalView extends AbstractTerminalView implements ProfileMenuView {

    @Override
    public void showInfo(String username, String nickname, int totalLevelsPassed,
                         int gemsCount, int coinsCount) {
        System.out.println("========== Your Profile ==========");
        System.out.println("Username: " + username);
        System.out.println("Nickname: " + nickname);
        System.out.println("Total Levels Passed: " + totalLevelsPassed);
        System.out.println("Gems: " + gemsCount);
        System.out.println("Coins: " + coinsCount);
        System.out.println("==================================");
    }

    @Override
    public void showUsernameChangeSuccess() {
        System.out.println("Username changed successfully!");
    }

    @Override
    public void showNicknameChangeSuccess() {
        System.out.println("Nickname changed successfully!");
    }

    @Override
    public void showEmailChangeSuccess() {
        System.out.println("Email changed successfully!");
    }

    @Override
    public void showPasswordChangeSuccess() {
        System.out.println("Password changed successfully!");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("profile menu");
    }
}
