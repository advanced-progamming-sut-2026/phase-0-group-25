package src.Model.Quests.QuestSubclasses;

import src.Enums.*;
import src.Model.Quests.Events.Event;
import src.Model.Quests.Events.LevelWonEvent;
import src.Model.Quests.Events.SunCollectedEvent;
import src.Model.Quests.Quest;
import src.Model.Quests.Reward;

import java.util.List;
import java.util.Map;


public class DefenselessCrossQuest extends Quest {
    private final int target;
    private final int required = 1;

    public DefenselessCrossQuest(String id, String name, String description,
                                 QuestCategory category, QuestPriority priority,
                                 List<Reward> rewards, Map<String, Object> conditions,
                                 boolean dailyReset, QuestPage page, int n) {
        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
        this.target = n;
    }

    @Override
    public int getRequiredCount() { return required; }

    @Override
    public void check(Event event) {
        if (!(event instanceof LevelWonEvent)) return;
        LevelWonEvent e = (LevelWonEvent) event;
        boolean[] emptyCols = e.getEmptyColumns();
        boolean[] emptyRows = e.getEmptyRows();
        if (emptyCols != null && emptyCols.length > target &&
                emptyRows != null && emptyRows.length > target &&
                emptyCols[target] && emptyRows[target]) {
            incrementProgress(1);
        }
    }
}