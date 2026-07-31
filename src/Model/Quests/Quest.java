package src.Model.Quests;

import src.Enums.*;
import src.Model.Quests.Events.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public abstract class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final QuestCategory category;
    private final QuestPriority priority;
    private final List<Reward> rewards;
    private final Map<String, Object> conditions;
    private final boolean dailyReset;
    private final QuestPage page;

    private int currentProgress;
    private boolean isCompleted;
    private boolean isClaimed;
    private LocalDate dateAssigned;

    protected Quest(String id, String name, String description,
                    QuestCategory category, QuestPriority priority,
                    List<Reward> rewards, Map<String, Object> conditions,
                    boolean dailyReset, QuestPage page) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.rewards = rewards;
        this.conditions = conditions;
        this.dailyReset = dailyReset;
        this.page = page;
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isClaimed = false;
        this.dateAssigned = dailyReset ? LocalDate.now() : null;
    }

    public abstract void check(Event event);
    public abstract int getRequiredCount();

    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public QuestCategory getCategory() { return category; }
    public QuestPriority getPriority() { return priority; }
    public List<Reward> getRewards() { return rewards; }
    public Map<String, Object> getConditions() { return conditions; }
    public boolean isDailyReset() { return dailyReset; }
    public QuestPage getPage() { return page; }

    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.min(progress, getRequiredCount());
    }

    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }

    public boolean isClaimed() { return isClaimed; }
    public void setClaimed(boolean claimed) { isClaimed = claimed; }

    public LocalDate getDateAssigned() { return dateAssigned; }
    public void setDateAssigned(LocalDate date) { this.dateAssigned = date; }

    
    protected void incrementProgress(int amount) {
        if (!isCompleted && !isClaimed) {
            currentProgress = Math.min(currentProgress + amount, getRequiredCount());
            if (currentProgress >= getRequiredCount()) {
                isCompleted = true;
            }
        }
    }

    public void reset() {
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isClaimed = false;
        this.dateAssigned = LocalDate.now();
    }
}