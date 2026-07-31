package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.ExplosiveUsedEvent;
import src.Model.Quests.Events.ZombieKilledEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.List;
import java.util.Map;

public class ProfessionalDemolisherQuest extends Quest {
    private final int required = 3;

    public ProfessionalDemolisherQuest(String id, String name, String description,
                                       QuestCategory category, QuestPriority priority,
                                       List<Reward> rewards, Map<String, Object> conditions,
                                       boolean dailyReset, QuestPage page) {
        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
    }

    @Override
    public int getRequiredCount() { return required; }

    @Override
    public void check(Event event) {
        if (!(event instanceof ExplosiveUsedEvent)) return;
        incrementProgress(1);
    }
}