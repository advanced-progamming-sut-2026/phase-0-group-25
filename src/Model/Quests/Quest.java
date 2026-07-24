package src.Model.Quests;

import src.Enums.QuestCategory;
import src.Enums.QuestEvent;
import src.Enums.QuestPriority;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Quest {
    private String id;
    private String name;
    private String description;
    private QuestCategory category;
    private QuestPriority priority;
    private QuestEvent triggerEvent;
    private int requiredCount;
    private int currentProgress;
    private boolean isCompleted;
    private boolean isClaimed;
    private List<Reward> rewards;
    private Map<String, Object> conditions; // for complex conditions (e.g., chapter, family, column)
    private LocalDate dateAssigned;          // for daily quests
    private boolean dailyReset;

    // Temporary fields for condition evaluation (can be replaced with Predicate)
    private Predicate<Object[]> conditionChecker;

    public Quest(String id, String name, String description, QuestCategory category,
                 QuestPriority priority, QuestEvent triggerEvent, int requiredCount,
                 List<Reward> rewards, Map<String, Object> conditions, boolean dailyReset) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.triggerEvent = triggerEvent;
        this.requiredCount = requiredCount;
        this.rewards = rewards;
        this.conditions = conditions;
        this.dailyReset = dailyReset;
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isClaimed = false;
        this.dateAssigned = dailyReset ? LocalDate.now() : null;
        // Set default condition checker (can be overridden per quest)
        this.conditionChecker = (data) -> true;
    }

    // Getters and setters
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

    public QuestEvent getTriggerEvent() {
        return triggerEvent;
    }

    public int getRequiredCount() {
        return requiredCount;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int progress) {
        this.currentProgress = Math.min(progress, requiredCount);
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public boolean isClaimed() {
        return isClaimed;
    }

    public void setClaimed(boolean claimed) {
        isClaimed = claimed;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Map<String, Object> getConditions() {
        return conditions;
    }

    public LocalDate getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(LocalDate date) {
        this.dateAssigned = date;
    }

    public boolean isDailyReset() {
        return dailyReset;
    }

    public void incrementProgress(int amount) {
        if (!isCompleted && !isClaimed) {
            currentProgress = Math.min(currentProgress + amount, requiredCount);
            if (currentProgress >= requiredCount) {
                isCompleted = true;
            }
        }
    }

    public boolean checkCompletion() {
        if (!isCompleted && currentProgress >= requiredCount) {
            isCompleted = true;
            return true;
        }
        return false;
    }

    public void reset() {
        this.currentProgress = 0;
        this.isCompleted = false;
        this.isClaimed = false;
        this.dateAssigned = LocalDate.now();
    }

    // Condition checker – can be set per quest
    public void setConditionChecker(Predicate<Object[]> checker) {
        this.conditionChecker = checker;
    }

    public boolean meetsConditions(Object... data) {
        return conditionChecker.test(data);
    }
}