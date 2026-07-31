package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.MowerTriggeredEvent;
import src.Model.Quests.Events.SunCollectedEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.List;
import java.util.Map;
public class MowerTimeQuest extends Quest {
    private final int required;

    public MowerTimeQuest(String id, String name, String description,
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
        if (!(event instanceof MowerTriggeredEvent)) return;
        MowerTriggeredEvent e = (MowerTriggeredEvent) event;
        incrementProgress(e.getKilledCount());
    }
}