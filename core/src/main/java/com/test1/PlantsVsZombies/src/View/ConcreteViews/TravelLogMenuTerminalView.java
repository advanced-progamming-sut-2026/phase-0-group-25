package com.test1.PlantsVsZombies.src.View.ConcreteViews;

import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.TravelLogMenuView;

import java.util.Comparator;
import java.util.List;

public class TravelLogMenuTerminalView extends AbstractTerminalView implements TravelLogMenuView {


    @Override
    public void showQuests(List<Quest> activeQuests, List<Quest> completedQuests, QuestPage page) {

        activeQuests.sort(Comparator.comparing(Quest::getPriority, Comparator.reverseOrder()));
        completedQuests.sort(Comparator.comparing(Quest::getPriority, Comparator.reverseOrder()));

        String pageDisplay = (page == null) ? "All Quests" : page.getCommandName().toUpperCase() + " QUESTS";
        System.out.println("========== " + pageDisplay + " ==========");

        System.out.println("Active Quests:");
        if (activeQuests.isEmpty()) {
            System.out.println("  No active quests.");
        } else {
            for (Quest q : activeQuests) {
                String status = q.getCurrentProgress() + "/" + q.getRequiredCount();
                System.out.printf("  [%s] %s (%s) - %s\n",
                        q.getPriority(), q.getName(), status, q.getDescription());
                System.out.println("    ID: " + q.getId());
            }
        }

        System.out.println("\nCompleted Quests (ready to claim):");
        if (completedQuests.isEmpty()) {
            System.out.println("  No completed quests.");
        } else {
            for (Quest q : completedQuests) {
                System.out.printf("  [%s] %s - %s\n",
                        q.getPriority(), q.getName(), q.getDescription());
                System.out.println("    ID: " + q.getId() + " (use 'claim quest -q <id>')");
            }
        }
        System.out.println("=====================================");
        System.out.println("Commands: travel log page <zombie|gardener|sun|challenges|minigames>");
    }

    @Override
    public void showMinigames() {
        System.out.println("========== MINIGAMES ==========");
        System.out.println("Available mini-games:");
        System.out.println("  - Vasebreaker");
        System.out.println("  - Walnut Bowling");
        System.out.println("  - I Zombie");
        System.out.println("To play, type: enter mini game <name>");
        System.out.println("=================================");
    }

    @Override
    public void showMiniGameLaunched(String miniGameName) {
        System.out.println("Launching mini-game: " + miniGameName);
    }

    @Override
    public void showRewardClaimed(String questId) {
        System.out.println("Reward for quest " + questId + " claimed successfully!");
    }

    @Override
    public void showCurrentMenu() {
        System.out.println("travel log menu");
    }
}
