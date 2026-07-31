package src.Model.Quests;

import src.Enums.QuestCategory;
import src.Enums.QuestEvent;
import src.Enums.QuestPriority;

import java.util.*;
import java.util.stream.Collectors;

public class QuestRepository {
    private final List<Quest> allQuests;
    private final Map<QuestEvent, List<Quest>> eventListeners;

    public QuestRepository(List<Quest> quests) {
        this.allQuests = new ArrayList<>(quests);
        this.eventListeners = new HashMap<>();
        registerEventListeners();
    }

    private void registerEventListeners() {
        for (Quest q : allQuests) {
            eventListeners.computeIfAbsent(q.getTriggerEvent(), k -> new ArrayList<>()).add(q);
        }
    }

    public List<Quest> getAllQuests() {
        return Collections.unmodifiableList(allQuests);
    }

    public List<Quest> getActiveQuests() {
        return allQuests.stream()
                .filter(q -> !q.isCompleted() && !q.isClaimed())
                .collect(Collectors.toList());
    }

    public List<Quest> getCompletedQuests() {
        return allQuests.stream()
                .filter(q -> q.isCompleted() && !q.isClaimed())
                .collect(Collectors.toList());
    }

    public List<Quest> getQuestsByCategory(QuestCategory category) {
        return allQuests.stream()
                .filter(q -> q.getCategory() == category)
                .collect(Collectors.toList());
    }

    public List<Quest> getQuestsByPriority(QuestPriority priority) {
        return allQuests.stream()
                .filter(q -> q.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public Optional<Quest> getQuestById(String id) {
        return allQuests.stream().filter(q -> q.getId().equals(id)).findFirst();
    }

    public List<Quest> getListenersForEvent(QuestEvent event) {
        return eventListeners.getOrDefault(event, Collections.emptyList());
    }

    // For use in loadProgress() – we need to iterate over all quests
    public Iterable<Quest> getAll() {
        return allQuests;
    }
}