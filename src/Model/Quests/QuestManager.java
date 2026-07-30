package src.Model.Quests;

import src.Enums.*;
import src.Model.News.News;
import src.Model.User.User;
import src.Model.User.UserProgress;
import src.Model.User.UsersManager;

import java.time.LocalDate;
import java.util.*;

public class QuestManager {
    private static QuestManager instance;
    private final QuestRepository repository;
    private User currentUser;

    private QuestManager() {
        List<Quest> quests = QuestDefinitionLoader.loadQuests();
        this.repository = new QuestRepository(quests);
        if (UsersManager.getInstance().getLoggedInUser() != null) {
            currentUser = UsersManager.getInstance().getLoggedInUser();
            loadProgress();
        }
    }

    public static QuestManager getInstance() {
        if (instance == null) {
            instance = new QuestManager();
        }
        return instance;
    }

    // ---------- Progress persistence ----------
    public void loadProgress() {
        if (currentUser == null) return;
        UserProgress progress = currentUser.getUserProgress();
        Map<String, Integer> questProgress = progress.getQuestProgress();
        List<String> completedIds = progress.getCompletedQuestIds();
        List<String> claimedIds = progress.getClaimedQuestIds();
        LocalDate lastDailyReset = progress.getLastDailyReset();

        LocalDate today = LocalDate.now();
        if (lastDailyReset == null || !lastDailyReset.equals(today)) {
            resetDailyQuests();
            UsersManager.getInstance().setLastDailyResetForCurrentUser(today);
        }

        for (Quest q : repository.getAll()) {
            if (questProgress.containsKey(q.getId())) {
                q.setCurrentProgress(questProgress.get(q.getId()));
            }
            if (completedIds.contains(q.getId())) {
                q.setCompleted(true);
            }
            if (claimedIds.contains(q.getId())) {
                q.setClaimed(true);
            }
            if (q.isDailyReset() && q.getDateAssigned() != null && !q.getDateAssigned().equals(today)) {
                q.reset();
                q.setDateAssigned(today);
            }
        }
    }

    public void saveProgress() {
        if (currentUser == null) return;
        UserProgress progress = currentUser.getUserProgress();
        Map<String, Integer> questProgress = new HashMap<>();
        List<String> completedIds = new ArrayList<>();
        List<String> claimedIds = new ArrayList<>();
        for (Quest q : repository.getAll()) {
            questProgress.put(q.getId(), q.getCurrentProgress());
            if (q.isCompleted()) completedIds.add(q.getId());
            if (q.isClaimed()) claimedIds.add(q.getId());
        }
        UsersManager um = UsersManager.getInstance();
        um.setQuestProgressForCurrentUser(questProgress);
        um.setCompletedQuestIdsForCurrentUser(completedIds);
        um.setClaimedQuestIdsForCurrentUser(claimedIds);
    }

    private void resetDailyQuests() {
        for (Quest q : repository.getAll()) {
            if (q.isDailyReset()) {
                q.reset();
            }
        }
    }

    // ---------- Event handling ----------
    public void notifyEvent(QuestEvent event, Object... data) {
        if (currentUser == null) {
            currentUser = UsersManager.getInstance().getLoggedInUser();
            if (currentUser == null) return;
            loadProgress();
        }

        List<Quest> listeners = repository.getListenersForEvent(event);
        if (listeners.isEmpty()) return;

        // Daily reset check
        LocalDate today = LocalDate.now();
        if (currentUser.getUserProgress().getLastDailyReset() == null ||
                !currentUser.getUserProgress().getLastDailyReset().equals(today)) {
            resetDailyQuests();
            UsersManager.getInstance().setLastDailyResetForCurrentUser(today);
        }

        for (Quest quest : listeners) {
            if (quest.isCompleted() || quest.isClaimed()) continue;
            if (!quest.meetsConditions(data)) continue;

            updateQuestProgress(quest, event, data);
            if (quest.checkCompletion()) {
                onQuestCompleted(quest);
            }
        }
        saveProgress();
    }

    private void updateQuestProgress(Quest quest, QuestEvent event, Object... data) {
        switch (event) {
            case ZOMBIE_KILLED:
            case PLANT_PLANTED:
            case EXPLOSIVE_USED:
            case PLANT_REMOVED:
            case PLANT_DESTROYED:
            case WAVE_COMPLETED:
            case LEVEL_WON:
            case LEVEL_LOST:
            case PLANT_FOOD_USED:
            case GAME_STARTED:
            case GAME_ENDED:
            case SYMMETRY_CHECK:
            case FAMILY_USED:
                quest.incrementProgress(1);
                break;
            case SUN_COLLECTED:
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
            case MOWER_TRIGGERED:
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
            default:
                quest.incrementProgress(1);
                break;
        }
    }

    private void onQuestCompleted(Quest quest) {
        // Apply rewards
        applyRewards(quest.getRewards());

        // Update leaderboard counters
        UsersManager um = UsersManager.getInstance();
        if (quest.getCategory() == QuestCategory.DAILY) {
            um.incrementDailyQuestsCompleted();
        } else {
            um.incrementNonDailyQuestsCompleted();
        }

        // Add news
        News news = new News("Quest completed: " + quest.getName() + "! Claim your rewards in Travel Log.");
        currentUser.getNewsManager().addNews(news);

        quest.setCompleted(true);
        saveProgress();
    }

    private void applyRewards(List<Reward> rewards) {
        UsersManager um = UsersManager.getInstance();
        for (Reward reward : rewards) {
            switch (reward.getType()) {
                case COINS:
                    um.addCoins(reward.getAmount());
                    break;
                case GEMS:
                    um.addGems(reward.getAmount());
                    break;
                case SEED_PACKETS:
                    if (reward.getPlantType() != null) {
                        um.addSeedPackets(reward.getPlantType(), reward.getAmount());
                    }
                    break;
                case UNLOCK_PLANT:
                    if (reward.getPlantType() != null) {
                        um.unlockPlant(reward.getPlantType());
                    }
                    break;
                case UNLOCK_CHAPTER:
                    if (reward.getChapterType() != null) {
                        um.unlockChapter(reward.getChapterType());
                    }
                    break;
                case PLANT_FOOD:
                    um.addPlantFood(reward.getAmount());
                    break;
                case POTS:
                    um.addPots(reward.getAmount());
                    break;
            }
        }
    }

    // ---------- Claim reward ----------
    public String claimReward(String questId) {
        Optional<Quest> opt = repository.getQuestById(questId);
        if (opt.isEmpty()) return "Quest not found.";
        Quest quest = opt.get();
        if (!quest.isCompleted()) return "Quest not completed yet.";
        if (quest.isClaimed()) return "Reward already claimed.";

        // Rewards were already applied on completion; just mark as claimed.
        quest.setClaimed(true);
        saveProgress();
        return null;
    }

    // ---------- Display methods ----------
    public List<Quest> getActiveQuests() {
        return repository.getActiveQuests();
    }

    public List<Quest> getCompletedQuests() {
        return repository.getCompletedQuests();
    }

    public List<Quest> getQuestsByCategory(QuestCategory category) {
        return repository.getQuestsByCategory(category);
    }

    public List<Quest> getQuestsByPriority(QuestPriority priority) {
        return repository.getQuestsByPriority(priority);
    }
}