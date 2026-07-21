package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.PlantsAndZombies.Position;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.GamePlayMenuView;

import java.util.regex.Matcher;

public class GamePlayMenu extends Menu {
    private final GamePlayMenuView gamePlayMenuView;
    private static GamePlay gamePlay;


    public GamePlayMenu(GamePlayMenuView gamePlayMenuView) {
        super(MenuType.Game);
        this.gamePlayMenuView = gamePlayMenuView;
    }


    public static GamePlay getGamePlay(){
        return gamePlay;
    }
    public static void setGamePlay(GamePlay gamePlay) {
        GamePlayMenu.gamePlay = gamePlay;
    }



    private void checkWinCondition() {
        if (gamePlay != null && gamePlay.checkingTheEndOfTheGame()) {
            System.out.println("Level completed successfully! Victory!");
            if (gamePlay.getLevelObject() != null) {
                gamePlay.getLevelObject().completeLevel();
            }
            MenuManager.getInstance().changeMenu(MenuType.Game);
        }
    }

    @Override
    public void handleSpecificCommands(String input) {
        if (gamePlay == null) {
            getView().showError("No active game play found. Returning to game menu.");
            MenuManager.getInstance().changeMenu(MenuType.Game);
            return;
        }

        Matcher matcher;
        if ((matcher = getMatcher(input, Command.AdvanceTime)) != null) {
            String count = matcher.group("count");
            thisGamePlay.advanceTime(Integer.parseInt(count));
        } else if ((matcher = getMatcher(input, Command.CollectSun)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                thisGamePlay.collectSun(x, y);
            }
        } else if ((matcher = getMatcher(input, Command.ShowSunAmount)) != null) {
            System.out.println("You have" + thisGamePlay.getMySuns() + "suns");
        } else if ((matcher = getMatcher(input, Command.CheatAddSuns)) != null) {
            String count = matcher.group("count");
            thisGamePlay.cheatAddSun(Integer.parseInt(count));
        } else if ((matcher = getMatcher(input, Command.CheatCooldown)) != null) {
            thisGamePlay.removeCooldown();
        } else if ((matcher = getMatcher(input, Command.CheatPlantFood)) != null) {
            thisGamePlay.addPlantFood();
        } else if ((matcher = getMatcher(input, Command.ReleaseTheNuke)) != null) {
            thisGamePlay.releaseTheNuke();
        } else if ((matcher = getMatcher(input, Command.PlantPlant)) != null) {
            String type = matcher.group("type");
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                Position thisPosition = new Position(x , y);
                BattlePlant thisPlant = thisGamePlay
                thisGamePlay.planting();
            }
        } else if ((matcher = getMatcher(input, Command.PluckPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                Position thisPosition = new Position(x , y);
                BattlePlant thisPlant = thisGamePlay
                thisGamePlay.plucking();
            }
        } else if ((matcher = getMatcher(input, Command.FeedPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                // TODO : how to use plant food...?
            }
        } else if ((matcher = getMatcher(input, Command.ShowMap)) != null) {
            thisGamePlay.showMap();
        } else if ((matcher = getMatcher(input, Command.ShowPlantsStatus)) != null) {
            thisGamePlay.showPlantsStatus();
        } else if ((matcher = getMatcher(input, Command.ShowTileStatus)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                Position thisPosition = new Position(x , y);
                thisGamePlay.showTileStatus(thisPosition);
            }
        } else {
            System.out.println("Unknown command: " + input);
        }
    }


    @Override
    public BaseView getView() {
        return gamePlayMenuView;
    }
}
