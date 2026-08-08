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
                l1.addPlantReward(PlantType.SUNFLOWER);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.WALL_NUT);

                Level l3 = new Level(type, 3, GamePlayType.SAVE_OUR_SEEDS);
                l3.addPlantReward(PlantType.POTATO_MINE);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.REPEATER);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case DARK_AGE: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.PUFF_SHROOM);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.SUN_SHROOM);

                Level l3 = new Level(type, 3, GamePlayType.NIGHT_OPS);
                l3.addPlantReward(PlantType.FUME_SHROOM);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.MAGNET_SHROOM);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case FROSTBITE_CAVES: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.HOT_POTATO);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.PEPPER_PULT);

                Level l3 = new Level(type, 3, GamePlayType.TIMED_WAR);
                l3.addPlantReward(PlantType.CHERRY_BOMB);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.FIRE_PEASHOOTER);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }
            case BIG_WAVE_BEACH: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.LILY_PAD);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.TANGLE_KELP);

                Level l3 = new Level(type, 3, GamePlayType.DEADLINE);
                l3.addPlantReward(PlantType.BOWLING_BULB);

                Level l4 = new Level(type, 4, GamePlayType.LOVE_YOUR_PLANTS);
                l4.addPlantReward(PlantType.CITRON);

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
