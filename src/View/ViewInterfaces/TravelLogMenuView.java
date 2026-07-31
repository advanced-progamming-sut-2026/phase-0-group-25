package src.View.ViewInterfaces;

import src.Enums.QuestPage;
import src.Model.Quests.Quest;

import java.util.List;

public interface TravelLogMenuView extends BaseView {
    void showQuests(List<Quest> activeQuests, List<Quest> completedQuests, QuestPage page);
    void showMinigames();

    void showMiniGameLaunched(String miniGameName);
    void showRewardClaimed(String questId);
}