package src.View.ConcreteViews;

import src.Model.Quests.Quest;
import src.View.ViewInterfaces.TravelLogMenuView;

import java.util.List;

public class TravelLogMenuTerminalView extends AbstractTerminalView implements TravelLogMenuView {

    @Override
    public void showQuests(List<Quest> activeQuests, List<Quest> completedQuests) {
        System.out.println("========== QUESTS ==========");
        System.out.println("Active Quests:");
        if (activeQuests.isEmpty()) {
            System.out.println("  No active quests.");
        } else {
            // Group by priority
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
        System.out.println("==============================");
    }

    @Override
    public void showMinigames() {
        System.out.println("Mini-games page - coming soon.");
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