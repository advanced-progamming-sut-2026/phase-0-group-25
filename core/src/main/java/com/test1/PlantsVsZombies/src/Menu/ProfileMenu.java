package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.ProfileMenuView;

import java.util.regex.Matcher;

public class ProfileMenu extends Menu {
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

    private void changePassword(String newPassword, String newPasswordConfirmed, String oldPassword) {
        String error = UsersManager.getInstance().validateAndChangePassword(newPassword, newPasswordConfirmed,oldPassword);
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
    public BaseView getView() {
        return profileMenuView;
    }
}
