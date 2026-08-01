package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class ProfessionalDemolisherQuest extends Quest {
    private final int required = 3;

    public ProfessionalDemolisherQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        super(id, category, priority, dailyReset, page);
        this.name = "Professional Demolisher";
        this.description = "Use 3 explosive plants in a single level.";
        this.reward = new Reward(RewardType.COINS, 100);
    }

    @Override public int getRequiredCount() { return required; }
    @Override public void check(Event event) {
        if (event instanceof ExplosiveUsedEvent) incrementProgress(1);
    }
}