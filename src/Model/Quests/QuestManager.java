package src.Model.Quests;

import src.Enums.*;
import src.Model.News.News;
import src.Model.Quests.Events.Event;
import src.Model.User.User;
import src.Model.User.UserProgress;
import src.Model.User.UsersManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private static QuestManager instance;
    private List<Quest> allQuests;
    private User currentUser;

    private QuestManager() {
        allQuests = QuestDefinitionLoader.loadQuests();
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

        for (Quest q : allQuests) {
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
        for (Quest q : allQuests) {
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
        for (Quest q : allQuests) {
            if (q.isDailyReset()) {
                q.reset();
            }
        }
    }

    
    public void notifyEvent(Event event) {
        if (currentUser == null) {
            currentUser = UsersManager.getInstance().getLoggedInUser();
            if (currentUser == null) return;
            loadProgress();
        }

        
        LocalDate today = LocalDate.now();
        if (currentUser.getUserProgress().getLastDailyReset() == null ||
                !currentUser.getUserProgress().getLastDailyReset().equals(today)) {
            resetDailyQuests();
            UsersManager.getInstance().setLastDailyResetForCurrentUser(today);
        }

        List<Quest> completedQuests = new ArrayList<>();
        for (Quest q : allQuests) {
            if (q.isCompleted() || q.isClaimed()) continue;
            q.check(event);
            if (q.isCompleted()) {
                completedQuests.add(q);
            }
        }

        for (Quest q : completedQuests) {
            onQuestCompleted(q);
        }
        saveProgress();
    }

    private void onQuestCompleted(Quest quest) {
        applyRewards(quest.getRewards());

        UsersManager um = UsersManager.getInstance();
        if (quest.getCategory() == QuestCategory.DAILY) {
            um.incrementDailyQuestsCompleted();
        } else {
            um.incrementNonDailyQuestsCompleted();
        }

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

    
    public String claimReward(String questId) {
        for (Quest q : allQuests) {
            if (q.getId().equals(questId)) {
                if (!q.isCompleted()) return "Quest not completed yet.";
                if (q.isClaimed()) return "Reward already claimed.";
                q.setClaimed(true);
                saveProgress();
                return null;
            }
        }
        return "Quest not found.";
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

    public List<Quest> getQuestsByPage(QuestPage page) {
        return allQuests.stream()
                .filter(q -> q.getPage() == page)
                .collect(Collectors.toList());
    }












}