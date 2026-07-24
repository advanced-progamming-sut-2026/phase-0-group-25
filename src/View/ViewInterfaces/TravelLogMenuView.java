package src.View.ViewInterfaces;

import src.Model.Quests.Quest;

import java.util.List;

public interface TravelLogMenuView extends BaseView {
    void showQuests(List<Quest> activeQuests, List<Quest> completedQuests);

    void showMinigames();

    void showRewardClaimed(String questId);
}