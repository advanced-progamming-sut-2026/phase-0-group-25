package src.Model.ChaptersAndLevels;

import src.Enums.ChapterType;
import src.Model.GamePlayType.*;

public class ChapterFactory {

    public static Chapter generateChapter(ChapterType type) {
        Chapter chapter = new Chapter(type);

        switch (type) {
            case ANCIENT_EGYPT:
                chapter.addLevel(new Level(1, new Simple()));
                //add levels here
                break;

            case DARK_AGE:
                chapter.addLevel(new Level(1, new Simple()));
                break;

            case FROSTBITE_CAVES:
                chapter.addLevel(new Level(1, new Simple()));
                break;

            case BIG_WAVE_BEACH:
                chapter.addLevel(new Level(1, new Simple()));
                break;
        }

        return chapter;
    }
}