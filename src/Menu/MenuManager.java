package src.Menu;

import src.Enums.MenuType;
import src.View.ConcreteViews.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MenuManager {
    private Scanner scanner;
    private Menu currentMenu;
    private static MenuManager instance;
    private final HashMap<MenuType, Menu> menusAndTheirNames;
    private boolean mustExit;

    public static MenuManager getInstance(){
        if(instance == null)
            instance = new MenuManager();
        return instance;
    }

    private MenuManager() {
        mustExit = false;
        scanner = new Scanner(System.in);
        menusAndTheirNames = new HashMap<>();
        menusAndTheirNames.put(MenuType.ChoosePlant, new ChoosePlantMenu(new ChoosePlantMenuTerminalView()));
        menusAndTheirNames.put(MenuType.CoinWallet, new CoinWalletMenu(new CoinWalletMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Collection, new CollectionMenu(new CollectionMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Game, new GameMenu(new GameMenuTerminalView()));
        menusAndTheirNames.put(MenuType.GamePlay, new GamePlayMenu(new GamePlayMenuTerminalView()));
        menusAndTheirNames.put(MenuType.GemWallet, new GemWalletMenu(new GemWalletMenuTerminalView()));
        menusAndTheirNames.put(MenuType.GreenHouse, new GreenHouseMenu(new GreenHouseMenuTerminalView()));
        menusAndTheirNames.put(MenuType.LeaderBoard, new LeaderBoardMenu(new LeaderBoardMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Login, new LoginMenu(new LoginMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Main, new MainMenu(new MainMenuTerminalView()));
        menusAndTheirNames.put(MenuType.News, new NewsMenu(new NewsMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Profile, new ProfileMenu(new ProfileMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Quest, new QuestMenu(new QuestMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Setting, new SettingMenu(new SettingMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Shop, new ShopMenu(new ShopMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Signup, new SignUpMenu(new SignUpMenuTerminalView()));
        menusAndTheirNames.put(MenuType.TravelLog, new TravelLogMenu(new TravelLogMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Network, new NetworkMenu(new NetworkMenuTerminalView()));
        currentMenu = menusAndTheirNames.get(MenuType.Signup);
    }

    public void setMustExit() {
        this.mustExit = true;
    }
    public void exitCurrentMenu(){
        currentMenu.exit();
    }

    public void changeMenu(MenuType menuType){
        this.currentMenu = menusAndTheirNames.get(menuType);
    }
    public void startAppLoop() {
        while (!mustExit) {
            String input = scanner.nextLine().trim();
            currentMenu.processCommand(input);
        }
    }
}
