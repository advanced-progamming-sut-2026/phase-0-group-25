package com.test1.PlantsVsZombies.src.Menu;

import com.badlogic.gdx.Screen;
import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.VaseBreaker;
import com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame.WalnutBowling;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.GamePlayScreen;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.VasebreakerScreen;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.WallnutBowlingScreen;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.GamePlayMenuView;

import java.util.regex.Matcher;

public class GamePlayMenu extends Menu {
    private static GamePlay gamePlay;
    private GamePlayMenuView gamePlayMenuView;

    public GamePlayMenu() {
        super(MenuType.Game);
    }

    /**
     * Initializes a new gameplay session, disposes of previous screen resources,
     * and attaches a fresh GamePlayScreen.
     */
    public void startSession(GamePlay gamePlay) {
        if (this.gamePlayMenuView instanceof Screen) {
            ((Screen) this.gamePlayMenuView).dispose();
        }
        GamePlayMenu.gamePlay = gamePlay;
        if (gamePlay instanceof VaseBreaker) {
            this.gamePlayMenuView = new VasebreakerScreen((VaseBreaker) gamePlay);
        } else if (gamePlay instanceof  WalnutBowling) {
            this.gamePlayMenuView = new WallnutBowlingScreen((WalnutBowling) gamePlay);
        } else {
            this.gamePlayMenuView = new GamePlayScreen(gamePlay);
        }
    }

    public void startSession(GamePlay gamePlay, GamePlayMenuView gamePlayMenuView) {
        if (this.gamePlayMenuView instanceof Screen) {
            ((Screen) this.gamePlayMenuView).dispose();
        }
        GamePlayMenu.gamePlay = gamePlay;
        this.gamePlayMenuView = gamePlayMenuView;
    }

    public static GamePlay getGamePlay() {
        return gamePlay;
    }

    public static void setGamePlay(GamePlay gamePlay) {
        GamePlayMenu.gamePlay = gamePlay;
    }

    public void setGamePlayMenuView(GamePlayMenuView gamePlayMenuView) {
        this.gamePlayMenuView = gamePlayMenuView;
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

    public void handleSpecificCommands(String input) {
        if (gamePlay == null) {
            getView().showError("No active game play found. Returning to game menu.");
            MenuManager.getInstance().changeMenu(MenuType.Game);
            return;
        }

        Matcher matcher;
        if ((matcher = getMatcher(input, Command.AdvanceTime)) != null) {
            String count = matcher.group("count");
            gamePlay.advanceTime(Integer.parseInt(count));
        } else if ((matcher = getMatcher(input, Command.CollectSun)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                gamePlay.collectSun(x, y);
            }
        } else if ((matcher = getMatcher(input, Command.ShowSunAmount)) != null) {
            System.out.println("You have " + gamePlay.getMySuns() + " suns");
        } else if ((matcher = getMatcher(input, Command.ShowPlantFoodAmount)) != null) {
            System.out.println("You have " + gamePlay.getNumOfPlantFood() + " plant foods");
        } else if ((matcher = getMatcher(input, Command.CheatAddSuns)) != null) {
            String count = matcher.group("count");
            gamePlay.cheatAddSun(Integer.parseInt(count));
        } else if ((matcher = getMatcher(input, Command.CheatCooldown)) != null) {
            gamePlay.removeCooldown();
        } else if ((matcher = getMatcher(input, Command.CheatPlantFood)) != null) {
            gamePlay.addPlantFood();
        } else if ((matcher = getMatcher(input, Command.ReleaseTheNuke)) != null) {
            gamePlay.releaseTheNuke();
        } else if ((matcher = getMatcher(input, Command.PlantPlant)) != null) {
            String type = matcher.group("type");
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y! (for x : 1 to 9 & for y : 1 to 5");
            } else if (!gamePlay.getPlants().stream().anyMatch(plant -> plant.getName().equals(type))) {
                System.out.println("You can't plant this plant! You didn't select this plant before the game!");
            } else {
                Position thisPosition = new Position(x, y);
                BattlePlant thisPlant = gamePlay.getPlants().stream()
                    .filter(plant -> plant.getName().equals(type)).findFirst().orElse(null);
                gamePlay.planting(thisPlant, thisPosition);
            }
        } else if ((matcher = getMatcher(input, Command.PluckPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                Position thisPosition = new Position(x, y);
                gamePlay.plucking(thisPosition);
            }
        } else if ((matcher = getMatcher(input, Command.FeedPlant)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else if (gamePlay.getTileByPosition(x, y).getPlants().isEmpty()) {
                System.out.println("There is no plant in this tile!!");
            } else {
                gamePlay.applyPlantFood(x, y);
            }
        } else if ((matcher = getMatcher(input, Command.ShowMap)) != null) {
            gamePlay.showMap();
        } else if ((matcher = getMatcher(input, Command.ShowPlantsStatus)) != null) {
            gamePlay.showPlantsStatus();
        } else if ((matcher = getMatcher(input, Command.ShowTileStatus)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (x > 9 || x < 1 || y > 5 || y < 1) {
                System.out.println("Pls type valid x and y!");
            } else {
                Position thisPosition = new Position(x, y);
                gamePlay.showTileStatus(thisPosition);
            }
        } else if ((matcher = getMatcher(input, Command.BreakJar)) != null) {
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (gamePlay instanceof VaseBreaker) {
                ((VaseBreaker) gamePlay).breakJar(x, y);
            } else {
                System.out.println("This command is only available in the Vasebreaker mini-game!");
            }
        } else if ((matcher = getMatcher(input, Command.PlantWalnut)) != null) {
            int index = Integer.parseInt(matcher.group("index"));
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (gamePlay instanceof WalnutBowling) {
                ((WalnutBowling) gamePlay).plantWalnut(index, x, y);
            } else {
                System.out.println("This command is only available in the Wallnut Bowling mini-game!");
            }
        } else if ((matcher = getMatcher(input, Command.PlaceZombie)) != null) {
            String type = matcher.group("type");
            int x = Integer.parseInt(matcher.group("x"));
            int y = Integer.parseInt(matcher.group("y"));
            if (gamePlay instanceof IZombie) {
                ((IZombie) gamePlay).placeZombie(type, x, y);
            } else {
                System.out.println("This command is only available in the I, Zombie mini-game!");
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
