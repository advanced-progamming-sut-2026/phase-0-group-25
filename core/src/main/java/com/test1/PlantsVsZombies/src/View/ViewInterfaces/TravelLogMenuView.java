package com.test1.PlantsVsZombies.src.View.ViewInterfaces;

import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;

import java.util.List;

public interface TravelLogMenuView extends BaseView {
    void showQuests(List<Quest> activeQuests, List<Quest> completedQuests, QuestPage page);

    void showMinigames();

    void showMiniGameLaunched(String miniGameName);

    void showRewardClaimed(String questId);
}
