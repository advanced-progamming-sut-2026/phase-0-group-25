package Menu;

import Enums.Command;
import Enums.MenuType;
import Model.User.User;
import Model.User.UsersManager;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.ProfileMenuView;

import java.util.regex.Matcher;

public class ProfileMenu extends Menu{
    private final ProfileMenuView profileMenuView;

    public ProfileMenu(ProfileMenuView profileMenuView) {
        super(MenuType.Main);
        this.profileMenuView = profileMenuView;
    }

    private void changeUsername(String newUsername) {
        String error = UsersManager.getInstance().validateAndChangeUsername(newUsername);
        if (error != null) {
            getView().showError(error);
            return;
        }
        profileMenuView.showUsernameChangeSuccess();
    }

    private void changeNickname(String newNickname) {
        String error = UsersManager.getInstance().validateAndChangeNickname(newNickname);
        if (error != null) {
            getView().showError(error);
            return;
        }
        profileMenuView.showNicknameChangeSuccess();
    }

    private void changeEmail(String newEmail) {
        String error = UsersManager.getInstance().validateAndChangeEmail(newEmail);
        if (error != null) {
            getView().showError(error);
            return;
        }
        profileMenuView.showEmailChangeSuccess();
    }

    private void changePassword(String newPassword, String oldPassword) {
        String error = UsersManager.getInstance().validateAndChangePassword(newPassword, oldPassword);
        if (error != null) {
            getView().showError(error);
            return;
        }
        profileMenuView.showPasswordChangeSuccess();
    }

    private void showProfileInfo() {
        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null) {
            getView().showError("No logged in user found.");
            return;
        }

        String username = currentUser.getUserName();
        String nickname = currentUser.getNickName();
        int totalLevelsPassed = currentUser.getUserProgress().extractTotalLevelsPassed();
        int gemsCount = currentUser.getUserProgress().getGemsCount();
        int coinsCount = currentUser.getUserProgress().getCoinsCount();

        profileMenuView.showInfo(username, nickname, totalLevelsPassed, gemsCount, coinsCount);
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.ChangeUsername)) != null) {
            changeUsername(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.ChangeNickname)) != null) {
            changeNickname(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.ChangeEmail)) != null) {
            changeEmail(matcher.group(1));
            return;
        }

        if ((matcher = getMatcher(input, Command.ChangePassword)) != null) {
            String newPassword = matcher.group(1);
            String oldPassword = matcher.group(2);
            changePassword(newPassword, oldPassword);
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowProfileInfo)) != null) {
            showProfileInfo();
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return profileMenuView;
    }
}
