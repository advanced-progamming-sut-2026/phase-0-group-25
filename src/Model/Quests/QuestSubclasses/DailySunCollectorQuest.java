package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class DailySunCollectorQuest extends Quest {
    private int required;
    private static final int[] TIERS = {3000, 4000, 5000};

    public DailySunCollectorQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg); randomizeVariable();
    }

    @Override public void randomizeVariable() {
        this.required = TIERS[new Random().nextInt(TIERS.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Daily Sun Collector";
        this.description = "Collect " + required + " suns in a single day.";
        this.reward = new Reward(RewardType.COINS, required / 100);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event e) { if (e instanceof SunCollectedEvent) incrementProgress(((SunCollectedEvent) e).getAmount()); }

    @Override public String getQuestVariable() { return String.valueOf(required); }
    @Override public void setQuestVariable(String v) { this.required = Integer.parseInt(v); updateDetails(); }
}