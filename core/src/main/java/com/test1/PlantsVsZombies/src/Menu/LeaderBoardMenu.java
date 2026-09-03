package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.SortColumn;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UserSorter;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class LeaderBoardMenu extends Menu {
    private final LeaderBoardMenuView view;
    private SortColumn sortColumn = SortColumn.CHAPTER;
    private boolean ascending = false;

    public LeaderBoardMenu(LeaderBoardMenuView view) {
        super(MenuType.Game);
        this.view = view;
    }

    /**
     * Returns the sorted list of users based on the specified column and direction.
     */
    public static List<User> getSortedUsers(SortColumn column, boolean ascending) {
        Collection<User> allUsers = UsersManager.getInstance().getAllUsers();
        return UserSorter.sortUsers(allUsers, column, ascending);
    }

    /**
     * Extracts and formats the last chapter and level played for a given user.
     */
    public static String getLastChapterAndLevel(User user) {
        if (user == null || user.getUserProgress() == null) return "None";
        UserProgress progress = user.getUserProgress();
        Map<ChapterType, Integer> unlocked = progress.getUnlockedChaptersAndLevels();
        if (unlocked == null || unlocked.isEmpty()) return "None";

        ChapterType highestChapter = null;
        int maxChapterNum = -1;

        for (ChapterType ct : unlocked.keySet()) {
            if (ct != ChapterType.MINI_GAME && ct.getChapterNumber() > maxChapterNum && unlocked.get(ct) > 0) {
                maxChapterNum = ct.getChapterNumber();
                highestChapter = ct;
            }
        }

        if (highestChapter == null) return "None";

        int level = unlocked.getOrDefault(highestChapter, 1);
        String formattedChapter = formatName(highestChapter.getName());
        return formattedChapter + " - Lvl " + level;
    }

    private static String formatName(String rawName) {
        if (rawName == null) return "Unknown";
        String[] words = rawName.split("_|\\s+");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) {
                sb.append(Character.toUpperCase(w.charAt(0)))
                    .append(w.substring(1).toLowerCase())
                    .append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public void onEnter() {
        showLeaderBoard();
    }

    public void handleSpecificCommands(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.SortUsers)) != null) {
            String columnName = matcher.group(1);
            SortColumn col = SortColumn.fromCommandName(columnName);
            if (col == null) {
                view.showError("Invalid sort column. Allowed: chapter, minigames, daily, nondaily.");
                return;
            }
            sortColumn = col;
            ascending = matcher.group(2).equalsIgnoreCase("asc");
            showLeaderBoard();
            return;
        }
        view.showError("Invalid command. Use: sort -c <column> -o <asc/desc>");
    }

    public void showLeaderBoard() {
        List<User> sorted = getSortedUsers(sortColumn, ascending);
        view.showLeaderBoard(sorted, sortColumn, ascending);
    }

    @Override
    public BaseView getView() {
        return view;
    }
}
