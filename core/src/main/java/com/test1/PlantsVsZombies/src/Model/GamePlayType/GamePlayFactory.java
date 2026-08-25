package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.GamePlayType;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.util.ArrayList;
import java.util.Set;

public class GamePlayFactory {

    public static GamePlay createGamePlay(GamePlayType type, ChapterType chapterType, int level, int difficulty, User user, ArrayList<String> plants, ArrayList<String> zombies
            , Set<String> boosted) {
        if (type == null) {
            type = GamePlayType.SIMPLE;
        }

        switch (type) {
            case SAVE_OUR_SEEDS:
                return new SaveOurSeeds(chapterType, level, difficulty, user, plants, zombies, boosted);
            case PLANT_WHAT_YOU_GET:
                return new PlantWhatYouGet(chapterType, level, difficulty, user, plants, zombies, boosted);
            case TIMED_WAR:
                return new TimedWar(chapterType, level, difficulty, user, plants, zombies, boosted);
            case DEADLINE:
                return new DeadLine(chapterType, level, difficulty, user, plants, zombies, boosted);
            case LOVE_YOUR_PLANTS:
                return new LoveYourPlants(chapterType, level, difficulty, user, plants, zombies, boosted);
            case CONVEYOR_BELT:
                return new ConveyorBelt(chapterType, level, difficulty, user, plants, zombies, boosted);
            case SIMPLE:
            default:
                return new Simple(chapterType, level, difficulty, user, plants, zombies, boosted);
        }
    }
}
