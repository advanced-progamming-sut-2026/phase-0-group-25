package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.LoginMenuView;

public class LoginMenu extends Menu {
    private final LoginMenuView loginMenuView;
    private String resettingUsername = null;

    public LoginMenu(LoginMenuView loginMenuView) {
        super(MenuType.Signup);
        this.loginMenuView = loginMenuView;
    }


    public void loginUser(String username, String password, boolean stayLoggedIn) {

        String authError = UsersManager.getInstance().authenticateUser(username, password, stayLoggedIn);
        if (authError != null) {
            getView().showError(authError);
            return;
        }
        loginMenuView.showLoginSuccess(UsersManager.getInstance().getLoggedInUser().getNickName());
        MenuManager.getInstance().changeMenu(MenuType.Main);
    }

    public void forgetPassword(String username, String email, String answer) {
        String error = UsersManager.getInstance().validateForgetPasswordRequest(username, email, answer);
        if (error != null) {
            getView().showError(error);
            return;
        }
        this.resettingUsername = username;
        loginMenuView.showPromptForNewPassword();
    }

    public void setNewPassword(String newPassword, String confirmPassword) {

        if (newPassword == null || newPassword.trim().isEmpty()) {
            getView().showError("Password cannot be empty.");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            getView().showError("Security answer verification does not match original field.");
            return;
        }
        String updateError = UsersManager.getInstance().updateUserPassword(resettingUsername, newPassword);
        if (updateError != null) {
            getView().showError(updateError);
            return;
        }
        this.resettingUsername = null;
        loginMenuView.showPasswordResetSuccess();
    }

    public void cancelPasswordReset() {
        this.resettingUsername = null;
    }

    @Override
    public BaseView getView() {
        return loginMenuView;
    }
}
