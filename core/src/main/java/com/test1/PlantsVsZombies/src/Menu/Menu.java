package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Menu {
    private final MenuType previousMenu;
    private final ArrayList<MenuType> changeableMenuTypes;


    public Menu(MenuType previousMenu) {
        this.previousMenu = previousMenu;
        changeableMenuTypes = new ArrayList<>();
    }

    public void addChangeableMenuType(MenuType menuType) {
        changeableMenuTypes.add(menuType);
    }

    public void exit() {
        MenuManager.getInstance().changeMenu(previousMenu);
    }

    public void onEnter() {
    }

    public Matcher getMatcher(String input, Command command) {
        Matcher matcher = Pattern.compile(command.getRegex()).matcher(input);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }

    public abstract BaseView getView();

}
