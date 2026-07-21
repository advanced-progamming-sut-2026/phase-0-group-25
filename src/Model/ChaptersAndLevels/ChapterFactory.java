// file: src/Model/ChaptersAndLevels/ChapterFactory.java
package src.Model.ChaptersAndLevels;

import src.Enums.ChapterType;
import src.Enums.GamePlayType;
import src.Enums.PlantType;
import src.Enums.ZombieType;

public class ChapterFactory {

    public static Chapter generateChapter(ChapterType type) {
        Chapter chapter = new Chapter(type);

        switch (type) {
            case ANCIENT_EGYPT: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.SUNFLOWER);
                l1.addZombieReward(ZombieType.CONE_HEAD);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.WALL_NUT);
                l2.addZombieReward(ZombieType.BUCKET_HEAD);

                Level l3 = new Level(type, 3, GamePlayType.SAVE_OUR_SEEDS);
                l3.addPlantReward(PlantType.POTATO_MINE);
                l3.addZombieReward(ZombieType.RA);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.REPEATER);
                l4.addZombieReward(ZombieType.TOMB_RAISER);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }

            case DARK_AGE: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.PUFF_SHROOM);
                l1.addZombieReward(ZombieType.KNIGHT);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.SUN_SHROOM);
                l2.addZombieReward(ZombieType.WIZARD);

                Level l3 = new Level(type, 3, GamePlayType.NIGHT_OPS);
                l3.addPlantReward(PlantType.FUME_SHROOM);
                l3.addZombieReward(ZombieType.KING);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.MAGNET_SHROOM);
                l4.addZombieReward(ZombieType.IMP_DRAGON);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }

            case FROSTBITE_CAVES: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.HOT_POTATO);
                l1.addZombieReward(ZombieType.HUNTER);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.PEPPER_PULT);
                l2.addZombieReward(ZombieType.DODO);

                Level l3 = new Level(type, 3, GamePlayType.TIMED_WAR);
                l3.addPlantReward(PlantType.CHERRY_BOMB);
                l3.addZombieReward(ZombieType.TROGLOBITE);

                Level l4 = new Level(type, 4, GamePlayType.SIMPLE);
                l4.addPlantReward(PlantType.FIRE_PEASHOOTER);
                l4.addZombieReward(ZombieType.GARGANTUAR);

                chapter.addLevel(l1);
                chapter.addLevel(l2);
                chapter.addLevel(l3);
                chapter.addLevel(l4);
                break;
            }

            case BIG_WAVE_BEACH: {
                Level l1 = new Level(type, 1, GamePlayType.SIMPLE);
                l1.addPlantReward(PlantType.LILY_PAD);
                l1.addZombieReward(ZombieType.SNORKEL);

                Level l2 = new Level(type, 2, GamePlayType.SIMPLE);
                l2.addPlantReward(PlantType.TANGLE_KELP);
                l2.addZombieReward(ZombieType.FISHERMAN);

                Level l3 = new Level(type, 3, GamePlayType.DEADLINE);
                l3.addPlantReward(PlantType.BOWLING_BULB);
                l3.addZombieReward(ZombieType.OCTOPUS);

                Level l4 = new Level(type, 4, GamePlayType.LOVE_YOUR_PLANTS);
                l4.addPlantReward(PlantType.CITRON);
                l4.addZombieReward(ZombieType.JUGGLER);

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