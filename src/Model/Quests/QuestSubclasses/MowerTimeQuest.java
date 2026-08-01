package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class MowerTimeQuest extends Quest {
    private int required;
    private static final int[] TIERS = {10, 20, 30, 40, 50};

    public MowerTimeQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg); randomizeVariable();
    }

    @Override public void randomizeVariable() {
        this.required = TIERS[new Random().nextInt(TIERS.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Mower Time";
        this.description = "Kill at least " + required + " zombies using mowers.";
        this.reward = new Reward(RewardType.GEMS, required);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event e) {
        if (e instanceof MowerTriggeredEvent) incrementProgress(((MowerTriggeredEvent) e).getKilledCount());
    }

    @Override public String getQuestVariable() { return String.valueOf(required); }
    @Override public void setQuestVariable(String v) { this.required = Integer.parseInt(v); updateDetails(); }
}