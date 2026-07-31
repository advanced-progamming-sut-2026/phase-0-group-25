package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.SunCollectedEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.List;
import java.util.Map;

public class DailySunCollectorQuest extends Quest {
    private final int required;

    public DailySunCollectorQuest(String id, String name, String description,
                                  QuestCategory category, QuestPriority priority,
                                  List<Reward> rewards, Map<String, Object> conditions,
                                  boolean dailyReset, QuestPage page, int required) {
        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
        this.required = required;
    }

    @Override
    public int getRequiredCount() { return required; }

    @Override
    public void check(Event event) {
        if (!(event instanceof SunCollectedEvent)) return;
        SunCollectedEvent e = (SunCollectedEvent) event;
        incrementProgress(e.getAmount());
    }
}