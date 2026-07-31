package src.Menu;

import src.Enums.MenuType;
import src.Model.User.UsersManager;
import src.View.ConcreteViews.*;

import java.util.HashMap;
import java.util.Scanner;

public class MenuManager {
    private static MenuManager instance;
    private final HashMap<MenuType, Menu> menusAndTheirNames;
    private Scanner scanner;
    private Menu currentMenu;
    private boolean mustExit;

    private MenuManager() {
        mustExit = false;
        scanner = new Scanner(System.in);
        menusAndTheirNames = new HashMap<>();
        menusAndTheirNames.put(MenuType.CoinWallet, new CoinWalletMenu(new CoinWalletMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Collection, new CollectionMenu(new CollectionMenuTerminalView()));
        GameMenu gameMenu = new GameMenu(new GameMenuTerminalView());
        menusAndTheirNames.put(MenuType.Game, gameMenu);
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
        menusAndTheirNames.put(MenuType.ChoosePlant, new ChoosePlantMenu(
                new ChoosePlantMenuTerminalView(),
                gameMenu.getPlantsStr(),
                gameMenu.getBoostedPlants()
        ));

        UsersManager usersManager = UsersManager.getInstance();
        if (usersManager.checkAndLoadStayLoggedIn()) {
            this.currentMenu = menusAndTheirNames.get(MenuType.Main);
        } else {
            this.currentMenu = menusAndTheirNames.get(MenuType.Signup);
        }
    }

    public static MenuManager getInstance() {
        if (instance == null)
            instance = new MenuManager();
        return instance;
    }

    public void setMustExit() {
        this.mustExit = true;
    }

    public void exitCurrentMenu() {
        currentMenu.exit();
    }

    public void changeMenu(MenuType menuType) {
        this.currentMenu = menusAndTheirNames.get(menuType);
        this.currentMenu.onEnter();
    }

    public GameMenu getGameMenu() {
        return (GameMenu) menusAndTheirNames.get(MenuType.Game);
    }

    public void startAppLoop() {
        while (!mustExit) {
            String input = scanner.nextLine().trim();
            currentMenu.processCommand(input);
        }
    }
}