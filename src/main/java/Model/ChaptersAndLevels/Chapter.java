package Model.ChaptersAndLevels;

import Enums.ChapterType;

import java.util.ArrayList;

public class Chapter {
    private ChapterType chapterType;
    private ArrayList<Level> levels;

    public Chapter(ChapterType chapterType) {
        this.chapterType = chapterType;
        this.levels = new ArrayList<>();
    }


    public void addLevel(Level level) {
        this.levels.add(level);
    }
    public void makeGame(){

    }
}
