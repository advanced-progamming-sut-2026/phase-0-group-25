package com.test1.PlantsVsZombies.src.Menu;

import com.badlogic.gdx.Screen;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ConcreteViews.*;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.*;
import java.util.HashMap;

public class MenuManager {
    private static MenuManager instance;
    private final HashMap<MenuType, Menu> menusAndTheirNames;
    private Menu currentMenu;

    private MenuManager() {
        menusAndTheirNames = new HashMap<>();

        // Concrete terminal views for non-refactored menus
        menusAndTheirNames.put(MenuType.CoinWallet, new CoinWalletMenu(new CoinWalletMenuTerminalView()));

        CollectionMenuScreen collectionMenuScreen = new CollectionMenuScreen();
        CollectionMenu collectionMenu = new CollectionMenu(collectionMenuScreen);
        collectionMenuScreen.setMenuController(collectionMenu);
        menusAndTheirNames.put(MenuType.Collection, collectionMenu);

        // Game / Chapter Selection Screen
        // Game / Chapter Selection Screens
        ChooseChapterScreen chooseChapterScreen =
            new ChooseChapterScreen();

        GameMenu gameMenu =
            new GameMenu(chooseChapterScreen);

        chooseChapterScreen.setMenuController(gameMenu);

// Level selection screen shown after clicking a chapter
        GameScreen gameLevelScreen =
            new GameScreen(gameMenu);

        gameMenu.setLevelSelectionView(
            gameLevelScreen
        );

        menusAndTheirNames.put(
            MenuType.Game,
            gameMenu
        );

        menusAndTheirNames.put(MenuType.GamePlay, new GamePlayMenu());
        menusAndTheirNames.put(MenuType.GemWallet, new GemWalletMenu(new GemWalletMenuTerminalView()));
        menusAndTheirNames.put(MenuType.GreenHouse, new GreenHouseMenu(new GreenHouseMenuTerminalView()));
        menusAndTheirNames.put(MenuType.LeaderBoard, new LeaderBoardMenu(new LeaderBoardMenuTerminalView()));

        SignUpMenuScreen signUpMenuScreen = new SignUpMenuScreen();
        SignUpMenu signUpMenu = new SignUpMenu(signUpMenuScreen);
        signUpMenuScreen.setMenuController(signUpMenu);
        menusAndTheirNames.put(MenuType.Signup, signUpMenu);

        LoginMenuScreen loginMenuScreen = new LoginMenuScreen();
        LoginMenu loginMenu = new LoginMenu(loginMenuScreen);
        loginMenuScreen.setMenuController(loginMenu);
        menusAndTheirNames.put(MenuType.Login, loginMenu);

        MainMenuScreen mainMenuScreen = new MainMenuScreen();
        MainMenu mainMenu = new MainMenu(mainMenuScreen);
        mainMenuScreen.setMenuController(mainMenu);
        menusAndTheirNames.put(MenuType.Main, mainMenu);

        menusAndTheirNames.put(MenuType.News, new NewsMenu(new NewsMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Profile, new ProfileMenu(new ProfileMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Quest, new QuestMenu(new QuestMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Setting, new SettingMenu(new SettingMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Shop, new ShopMenu(new ShopMenuTerminalView()));
        menusAndTheirNames.put(MenuType.TravelLog, new TravelLogMenu(new TravelLogMenuTerminalView()));
        menusAndTheirNames.put(MenuType.Network, new NetworkMenu(new NetworkMenuTerminalView()));
        ChoosePlantScreen choosePlantScreen = new ChoosePlantScreen();
        ChoosePlantMenu choosePlantMenu = new ChoosePlantMenu(
            choosePlantScreen,
            gameMenu.getPlantsStr()
        );
        choosePlantScreen.setMenuController(choosePlantMenu);
        choosePlantScreen.setGameMenu(gameMenu);
        menusAndTheirNames.put(MenuType.ChoosePlant, choosePlantMenu);

        UsersManager usersManager = UsersManager.getInstance();
        if (usersManager.checkAndLoadStayLoggedIn()) {
            this.currentMenu = menusAndTheirNames.get(MenuType.Main);
        } else {
            this.currentMenu = menusAndTheirNames.get(MenuType.Signup);
        }
    }

    public static MenuManager getInstance() {
        if (instance == null) {
            instance = new MenuManager();
        }
        return instance;
    }

    public void initInitialScreen() {
        if (currentMenu != null && currentMenu.getView() instanceof Screen) {
            UIManager.changeScreen((Screen) currentMenu.getView());
        }
    }

    public void exitCurrentMenu() {
        currentMenu.exit();
    }

    public void changeMenu(MenuType menuType) {
        this.currentMenu = menusAndTheirNames.get(menuType);
        this.currentMenu.onEnter();
        if (this.currentMenu.getView() instanceof Screen) {
            UIManager.changeScreen((Screen) this.currentMenu.getView());
        }
    }

    public GameMenu getGameMenu() {
        return (GameMenu) menusAndTheirNames.get(MenuType.Game);
    }

    public GamePlayMenu getGamePlayMenu() {
        return (GamePlayMenu) menusAndTheirNames.get(MenuType.GamePlay);
    }

}
