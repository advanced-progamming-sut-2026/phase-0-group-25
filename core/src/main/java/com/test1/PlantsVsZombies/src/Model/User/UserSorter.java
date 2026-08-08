package com.test1.PlantsVsZombies.src.Model.User;

import com.test1.PlantsVsZombies.src.Enums.SortColumn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class UserSorter {

    public static List<User> sortUsers(Collection<User> users, SortColumn sortColumn, boolean ascending) {
        List<User> userList = new ArrayList<>(users);
        Comparator<User> comparator;

        switch (sortColumn) {
            case CHAPTER:
                comparator = Comparator.comparingInt(UserSorter::getChapterProgress);
                break;
            case MINIGAMES:
                comparator = Comparator.comparingInt(u -> u.getUserProgress().getMiniGamesCompleted());
                break;
            case DAILY:
                comparator = Comparator.comparingInt(u -> u.getUserProgress().getDailyQuestsCompleted());
                break;
            case NONDAILY:
                comparator = Comparator.comparingInt(u -> u.getUserProgress().getNonDailyQuestsCompleted());
                break;
            default:
                comparator = Comparator.comparing(UserSorter::getChapterProgress);
                break;
        }

        if (!ascending) {
            comparator = comparator.reversed();
        }

        userList.sort(comparator);
        return userList;
    }

    private static int getChapterProgress(User user) {
        var progress = user.getUserProgress();
        int levelsCompleted = 0;
        for (int level : progress.getUnlockedChaptersAndLevels().values()) {
            levelsCompleted += level;
        }
        return Math.max(levelsCompleted - 1, 0);
    }
}
