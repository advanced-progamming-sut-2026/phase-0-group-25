package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

import java.util.ArrayList;

public interface NewsMenuView extends BaseView {

    void showUnreadNew(ArrayList<String> news);

    void showAllNews(ArrayList<String> news);
}
