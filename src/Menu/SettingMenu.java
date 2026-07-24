package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.SettingMenuView;

import java.util.regex.Matcher;

public class SettingMenu extends Menu {
    private final SettingMenuView settingMenuView;

    public SettingMenu(SettingMenuView settingMenuView) {
        super(MenuType.Main);
        this.settingMenuView = settingMenuView;
    }

    private void changeDifficulty(String difficultyLevel) {
        String error = UsersManager.getInstance().changeDifficulty(difficultyLevel);
        if (error != null) {
            getView().showError(error);
        }
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.ChangeDifficulty)) != null) {
            changeDifficulty(matcher.group(1));
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    @Override
    public BaseView getView() {
        return settingMenuView;
    }
}
