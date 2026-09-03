package com.test1.PlantsVsZombies.src.Model.ChaptersAndLevels;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.GamePlayType;
import com.test1.PlantsVsZombies.src.Enums.PlantType;

public class ChapterFactory {

    public static Chapter generateChapter(ChapterType type) {
        Chapter chapter = new Chapter(type);
        switch (type) {
            case ANCIENT_EGYPT: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);


                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);


                Level l3 = new Level(type, 3, GamePlayType.SAVE_OUR_SEEDS);


                Level l4 = new Level(type, 4, GamePlayType.ZOMBOSS);


                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case DARK_AGE: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);


                Level l2 = new Level(type, 2, GamePlayType.CONVEYOR_BELT);


                Level l3 = new Level(type, 3, GamePlayType.PLANT_WHAT_YOU_GET);


                Level l4 = new Level(type, 4, GamePlayType.ZOMBOSS);


                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case FROSTBITE_CAVES: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);


                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);


                Level l3 = new Level(type, 3, GamePlayType.TIMED_WAR);


                Level l4 = new Level(type, 4, GamePlayType.ZOMBOSS);


                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case BIG_WAVE_BEACH: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);


                Level l2 = new Level(type, 2, GamePlayType.LOVE_YOUR_PLANTS);


                Level l3 = new Level(type, 3, GamePlayType.DEADLINE);


                Level l4 = new Level(type, 4, GamePlayType.ZOMBOSS);


                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
        }
        return chapter;
    }
}
