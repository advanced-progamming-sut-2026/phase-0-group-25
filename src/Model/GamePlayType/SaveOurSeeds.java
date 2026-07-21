package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Model.User.User;

import java.util.ArrayList;

public class SaveOurSeeds extends GamePlay {

    public SaveOurSeeds(ChapterType chapterType, int level, int difficulty, User thisUser,
                        ArrayList<String> plants, ArrayList<String> zombies) {
        super(chapterType, level, difficulty, thisUser, plants, zombies);
    }

    @Override
    public void update() {
        // TODO simple update


    }
}
