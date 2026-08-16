package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.Command;
import com.test1.PlantsVsZombies.src.Enums.MenuType;
import com.test1.PlantsVsZombies.src.Enums.SortColumn;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserSorter;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.LeaderBoardMenuView;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;

public class LeaderBoardMenu extends Menu {
    private final LeaderBoardMenuView view;
    private SortColumn sortColumn = SortColumn.CHAPTER;
    private boolean ascending = true;

    public LeaderBoardMenu(LeaderBoardMenuView view) {
        super(MenuType.Game);
        this.view = view;
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
            ascending = matcher.group(2).equals("asc");
            showLeaderBoard();
            return;
        }
        view.showError("Invalid command. Use: sort -c <column> -o <asc/desc>");
    }

    private void showLeaderBoard() {
        Collection<User> allUsers = UsersManager.getInstance().getAllUsers();
        List<User> sorted = UserSorter.sortUsers(allUsers, sortColumn, ascending);
        view.showLeaderBoard(sorted, sortColumn, ascending);
    }

    @Override
    public BaseView getView() {
        return view;
    }
}
