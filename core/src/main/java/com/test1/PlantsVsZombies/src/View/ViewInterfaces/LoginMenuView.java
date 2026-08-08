package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

public interface LoginMenuView extends BaseView {

    void showPromptForNewPassword();

    void showPasswordResetSuccess();

    void showLoginSuccess(String nickname);
}
