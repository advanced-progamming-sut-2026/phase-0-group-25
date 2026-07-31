package src.View.ConcreteViews;

import src.Enums.ChapterType;
import src.Enums.SortColumn;
import src.Model.ChaptersAndLevels.Chapter;
import src.Model.User.User;
import src.Model.User.UserProgress;
import src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.List;
import java.util.Map;

public class LeaderBoardMenuTerminalView extends AbstractTerminalView implements LeaderBoardMenuView {

    @Override
    public void showLeaderBoard(List<User> users, SortColumn sortColumn, boolean ascending) {
        System.out.println("\n======================================" +
                " LEADERBOARD " +
                "==========================================");
        System.out.printf("Sorted by: %s (%s)\n",
                sortColumn.getCommandName(),
                ascending ? "ascending" : "descending");
        System.out.println("----------------------------------------------------" +
                "-----------------------------------------");
        System.out.printf("%-20s %-20s %-20s %-20s %-20s\n",
                "Username", "Last Chapter/Level", "Minigames", "Daily", "Non-Daily");
        System.out.println("--------------------------------------------------------" +
                "-------------------------------------");

        for (User user : users) {
            String last = formatLastChapterLevel(user);
            System.out.printf("%-20s %-20s %-20s %-20s %-20s\n",
                    user.getUserName(),
                    last,
                    user.getUserProgress().getMiniGamesCompleted(),
                    user.getUserProgress().getDailyQuestsCompleted(),
                    user.getUserProgress().getNonDailyQuestsCompleted());
        }
        System.out.println("===========================================" +
                "==================================================");
        System.out.println("Commands: sort -c <column> -o <asc/desc>");
        System.out.println("Columns: chapter, minigames, daily, nondaily");
    }

    private String formatLastChapterLevel(User user) {
        UserProgress progress = user.getUserProgress();
        int maxChapterNumber = 0;
        String result = "None";
        for (ChapterType chapterType:progress.getUnlockedChaptersAndLevels().keySet()){
            int lastUnlockedLevel = progress.getUnlockedChaptersAndLevels().get(chapterType);
            if(lastUnlockedLevel > 1)
                if(chapterType.getChapterNumber() > maxChapterNumber)
                    result = "chapter " + chapterType.getName() + " level " + (lastUnlockedLevel-1);
        }
        return result;
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("leader board menu");
    }
}