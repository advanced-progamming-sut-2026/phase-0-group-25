package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Events.ZombieKilledEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.List;
import java.util.Map;

public class CloudyDayQuest extends Quest {
    private final int required = 1;

    public CloudyDayQuest(String id, String name, String description,
                          QuestCategory category, QuestPriority priority,
                          List<Reward> rewards, Map<String, Object> conditions,
                          boolean dailyReset, QuestPage page) {
        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
    }

    @Override
    public int getRequiredCount() { return required; }

    @Override
    public void check(Event event) {
        if (!(event instanceof LevelWonEvent)) return;
        LevelWonEvent e = (LevelWonEvent) event;
        if (e.getSunProducersCount() == 3) {
            incrementProgress(1);
        }
    }
}