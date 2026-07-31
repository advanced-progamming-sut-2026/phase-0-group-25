package src.View.ViewInterfaces;

import src.Enums.SortColumn;
import src.Model.User.User;

import java.util.List;

public interface LeaderBoardMenuView extends BaseView {
    void showLeaderBoard(List<User> users, SortColumn sortColumn, boolean ascending);
}