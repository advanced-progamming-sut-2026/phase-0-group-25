package src.View.ConcreteViews;

import src.Model.User.User;
import src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.List;

public class LeaderBoardMenuTerminalView extends AbstractTerminalView implements LeaderBoardMenuView {

    @Override
    public void showLeaderBoard(List<User> users, String sortColumn, boolean ascending) {
        System.out.println("\n========== LEADERBOARD ==========");
        System.out.printf("Sorted by: %s (%s)\n", sortColumn, ascending ? "ascending" : "descending");
        System.out.println("------------------------------------");
        System.out.printf("%-20s %-18s %-10s %-8s %-10s\n",
                "Username", "Last Chapter/Level", "Minigames", "Daily", "Non-Daily");
        System.out.println("------------------------------------");

        for (User user : users) {
            String last = formatLastChapterLevel(user);
            System.out.printf("%-20s %-18s %-10d %-8d %-10d\n",
                    user.getUserName(),
                    last,
                    user.getUserProgress().getMiniGamesCompleted(),
                    user.getUserProgress().getDailyQuestsCompleted(),
                    user.getUserProgress().getNonDailyQuestsCompleted());
        }
        System.out.println("====================================");
        System.out.println("Commands: sort -c <column> -o <asc/desc>");
        System.out.println("Columns: username, chapter, minigames, daily, nondaily");
    }

    private String formatLastChapterLevel(User user) {
        var progress = user.getUserProgress();
        int maxValue = 0;
        String result = "None";
        for (var entry : progress.getUnlockedChaptersAndLevels().entrySet()) {
            int chapterIdx = entry.getKey().ordinal();
            int level = entry.getValue();
            if (level > 1) {
                int completed = level - 1;
                int value = chapterIdx * 10 + completed;
                if (value > maxValue) {
                    maxValue = value;
                    result = "Chapter " + (chapterIdx + 1) + ", Level " + completed;
                }
            }
        }
        return result;
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("leader board menu");
    }
}