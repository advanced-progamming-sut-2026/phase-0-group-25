package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Enums.GamePlayType;
import src.Model.PlantsAndZombies.BattlePlant;
import src.Model.User.User;

import java.util.ArrayList;

public class GamePlayFactory {

    public static GamePlay createGamePlay(GamePlayType type, ChapterType chapterType, int level, int difficulty, User user, ArrayList<String > plants, ArrayList<String> zombies) {
        if (type == null) {
            type = GamePlayType.SIMPLE;
        }

        switch (type) {
            case SAVE_OUR_SEEDS:
                return new SaveOurSeeds(chapterType, level, difficulty, user, plants, zombies);
            case NIGHT_OPS:
                return new NightOps(chapterType, level, difficulty, user, plants, zombies);
            case TIMED_WAR:
                return new TimedWar(chapterType, level, difficulty, user, plants, zombies);
            case DEADLINE:
                return new DeadLine(chapterType, level, difficulty, user, plants, zombies);
            case LOVE_YOUR_PLANTS:
                return new LoveYourPlants(chapterType, level, difficulty, user, plants, zombies);
            case SIMPLE:
            default:
                return new Simple(chapterType, level, difficulty, user, plants, zombies);
        }
    }
}