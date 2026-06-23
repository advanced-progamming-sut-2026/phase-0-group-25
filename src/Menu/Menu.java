package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.View.ViewInterfaces.BaseView;

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

    public Matcher getMatcher(String input, Command command) {
        Matcher matcher = Pattern.compile(command.getRegex()).matcher(input);
        if (matcher.matches()) {
            return matcher;
        }
        return null;
    }

    public void processCommand(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.Exit)) != null){
            MenuManager.getInstance().exitCurrentMenu();
            return;
        }
        else if((matcher = getMatcher(input, Command.changeMenu)) != null){
            for (MenuType menuType: changeableMenuTypes){
                if(matcher.group(1).equals(menuType.getString())){
                    MenuManager.getInstance().changeMenu(menuType);
                    return;
                }
            }
            getView().showError("menu not found!");
            return;
        }
        else if((matcher = getMatcher(input, Command.ShowMenu)) != null){
            getView().showCurrentMenu();
            return;
        }
        handleSpecificCommands(input);
    }

    public abstract void handleSpecificCommands(String input);
    public abstract BaseView getView();

}
