

package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.User.User;
import src.Model.User.UsersManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;

public class LeaderBoardMenu extends Menu {
    private final LeaderBoardMenuView view;
    private String sortColumn = "username";
    private boolean ascending = true;


    public LeaderBoardMenu(LeaderBoardMenuView view) {
        super(MenuType.Main);
        this.view = view;
    }

    @Override
    public void onEnter() {
        showLeaderBoard();
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;
        if ((matcher = getMatcher(input, Command.SortUsers)) != null) {
            sortColumn = matcher.group(1);
            ascending = matcher.group(2).equals("asc");
            showLeaderBoard();
            return;
        }
        
        view.showError("Invalid command. Use: sort -c <column> -o <asc/desc>");
    }

    private void showLeaderBoard() {
        Collection<User> allUsers = UsersManager.getInstance().getAllUsers();
        List<User> sorted = new ArrayList<>(allUsers);
        sorted.sort(getComparator());
        view.showLeaderBoard(sorted, sortColumn, ascending);
    }

    private Comparator<User> getComparator() {
        switch (sortColumn) {
            case "username":
                return Comparator.comparing(User::getUserName);
            case "chapter":
                return Comparator.comparing(u -> getChapterLevelValue(u));
            case "minigames":
                return Comparator.comparing(u -> u.getUserProgress().getMiniGamesCompleted());
            case "daily":
                return Comparator.comparing(u -> u.getUserProgress().getDailyQuestsCompleted());
            case "nondaily":
                return Comparator.comparing(u -> u.getUserProgress().getNonDailyQuestsCompleted());
            default:
                return Comparator.comparing(User::getUserName);
        }
    }

    private int getChapterLevelValue(User u) {
        
        var progress = u.getUserProgress();
        int max = 0;
        for (var entry : progress.getUnlockedChaptersAndLevels().entrySet()) {
            int chapterIdx = entry.getKey().ordinal(); 
            int level = entry.getValue(); 
            if (level > 1) {
                int completed = level - 1;
                int value = chapterIdx * 10 + completed;
                if (value > max) max = value;
            }
        }
        return max;
    }

    @Override
    public BaseView getView() {
        return view;
    }
}