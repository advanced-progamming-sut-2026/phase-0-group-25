package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.SettingMenuView;

public class SettingMenu extends Menu{
    private final SettingMenuView settingMenuView;

    public SettingMenu(SettingMenuView settingMenuView) {
        super(MenuType.Main);
        this.settingMenuView = settingMenuView;
    }

    public void changeDifficulty(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return settingMenuView;
    }
}
