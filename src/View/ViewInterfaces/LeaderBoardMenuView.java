package src.View.ViewInterfaces;

import src.Model.User.User;

import java.util.List;

public interface LeaderBoardMenuView extends BaseView {
    void showLeaderBoard(List<User> users, String sortColumn, boolean ascending);
}