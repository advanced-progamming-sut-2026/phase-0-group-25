package src.Menu;

import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.ProfileMenuView;

public class ProfileMenu extends Menu{
    private final ProfileMenuView profileMenuView;

    public ProfileMenu(ProfileMenuView profileMenuView) {
        super(MenuType.Main);
        this.profileMenuView = profileMenuView;
    }

    public void changeUserName(){

    }
    public void changeNickName(){

    }
    public void changeEmail(){

    }
    public void changePassword(){

    }

    @Override
    public void handleSpecificCommands(String input) {

    }

    @Override
    public BaseView getView() {
        return profileMenuView;
    }
}
