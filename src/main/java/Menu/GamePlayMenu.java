package Menu;

import Enums.Command;
import Enums.MenuType;
import View.ViewInterfaces.BaseView;
import View.ViewInterfaces.GameMenuView;
import View.ViewInterfaces.GamePlayMenuView;

import java.util.regex.Matcher;

public class GamePlayMenu extends Menu{
    private final GamePlayMenuView gamePlayMenuView;

    public GamePlayMenu(GamePlayMenuView gamePlayMenuView) {
        super(MenuType.Game);
        this.gamePlayMenuView = gamePlayMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.AdvanceTime)) != null) {
            String count = matcher.group("count");

        } else if ((matcher = getMatcher(input, Command.CollectSun)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {

            }
        } else if ((matcher = getMatcher(input, Command.ShowSunAmount)) != null) {

        } else if ((matcher = getMatcher(input, Command.CheatAddSuns)) != null) {
            String count = matcher.group("count");

        } else if ((matcher = getMatcher(input, Command.CheatCooldown)) != null) {

        } else if ((matcher = getMatcher(input, Command.CheatPlantFood)) != null) {

        } else if ((matcher = getMatcher(input, Command.ReleaseTheNuke)) != null) {

        } else if ((matcher = getMatcher(input, Command.PlantPlant)) != null) {
            String type = matcher.group("type");
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {

            }
        } else if ((matcher = getMatcher(input, Command.PluckPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {

            }
        } else if ((matcher = getMatcher(input, Command.FeedPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {

            }
        } else if ((matcher = getMatcher(input, Command.ShowMap)) != null) {

        } else if ((matcher = getMatcher(input, Command.ShowPlantsStatus)) != null) {

        } else if ((matcher = getMatcher(input, Command.ShowTileStatus)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {

            }
        } else if ((matcher = getMatcher(input, Command.StartGame)) != null) {

        } else {
            System.out.println("Unknown command: " + input);
        }
    }

    @Override
    public BaseView getView() {
        return gamePlayMenuView;
    }
}
