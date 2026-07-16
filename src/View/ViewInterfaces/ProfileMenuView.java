package src.View.ViewInterfaces;

public interface ProfileMenuView extends BaseView{
    void showInfo(String username, String nickname, int totalLevelsPassed,
                  int gemsCount, int coinsCount);
    void showUsernameChangeSuccess();
    void showNicknameChangeSuccess();
    void showEmailChangeSuccess();
    void showPasswordChangeSuccess();}
