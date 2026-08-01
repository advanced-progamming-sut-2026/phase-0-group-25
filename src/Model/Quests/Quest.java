package src.Model.Quests;

import src.Enums.QuestCategory;
import src.Enums.QuestPage;
import src.Enums.QuestPriority;
import src.Model.Quests.Events.Event;

import java.time.LocalDate;

public abstract class Quest {
    private final String id;
    private final QuestCategory category;
    private final QuestPriority priority;
    private final boolean dailyReset;
    private final QuestPage page;
    protected String name;
    protected String description;
    protected Reward reward;
    private int currentProgress;
    private boolean isCompleted;
    private boolean isClaimed;
    private LocalDate dateAssigned;

    protected Quest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
        this.id = id;
        this.category = category;
        this.priority = priority;
        this.dailyReset = dailyReset;
        this.page = page;
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isClaimed = false;
        this.dateAssigned = dailyReset ? LocalDate.now() : null;
    }

    public abstract void check(Event event);

    public abstract int getRequiredCount();

    public void randomizeVariable() {
    }


    public String getQuestVariable() {
        return null;
    }

    public void setQuestVariable(String variable) {
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public QuestPriority getPriority() {
        return priority;
    }

    public Reward getReward() {
        return reward;
    }

    public boolean isDailyReset() {
        return dailyReset;
    }

    public QuestPage getPage() {
        return page;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.min(progress, getRequiredCount());
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        this.isClaimed = claimed;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate date) {
        this.dateAssigned = date;
    }

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