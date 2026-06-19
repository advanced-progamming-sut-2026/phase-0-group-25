package src.Menu;

import src.Enums.MenuType;

import java.util.ArrayList;

public abstract class Menu {
    private MenuType previousMenu;
    private ArrayList<MenuType> changeableMenuTypes;
    public void addChangeableMenuType(MenuType menuType){
        changeableMenuTypes.add(menuType);
    }

    public void exit(){

    }
}
