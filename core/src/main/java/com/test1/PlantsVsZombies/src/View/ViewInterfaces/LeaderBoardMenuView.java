package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

import com.test1.PlantsVsZombies.src.Enums.SortColumn;
import com.test1.PlantsVsZombies.src.Model.User.User;

import java.util.List;

public interface LeaderBoardMenuView extends BaseView {
    void showLeaderBoard(List<User> users, SortColumn sortColumn, boolean ascending);
}
