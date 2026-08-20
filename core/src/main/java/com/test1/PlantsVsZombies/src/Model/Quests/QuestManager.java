package com.test1.PlantsVsZombies.src.Model.Quests;

import com.test1.PlantsVsZombies.src.Enums.PlantType;
import com.test1.PlantsVsZombies.src.Enums.QuestCategory;
import com.test1.PlantsVsZombies.src.Enums.QuestPage;
import com.test1.PlantsVsZombies.src.Model.News.News;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;

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


        Map<String, String> questVariables = progress.getQuestVariables();
        if (questVariables == null) questVariables = new HashMap<>();

        List<String> completedIds = progress.getCompletedQuestIds();
        List<String> claimedIds = progress.getClaimedQuestIds();
        LocalDate lastDailyReset = progress.getLastDailyReset();
        LocalDate today = LocalDate.now();

        boolean performedDailyReset = false;
        if (lastDailyReset == null || !lastDailyReset.equals(today)) {
            resetDailyQuests();
            UsersManager.getInstance().setLastDailyResetForCurrentUser(today);
            performedDailyReset = true;
        }

        for (Quest q : allQuests) {

            if (performedDailyReset && q.isDailyReset()) {
                q.setDateAssigned(today);
                continue;
            }

            if (questVariables.containsKey(q.getId())) {
                q.setQuestVariable(questVariables.get(q.getId()));
            }
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


        if (performedDailyReset) {
            saveProgress();
        }
    }

    public void saveProgress() {
        if (currentUser == null) return;
        Map<String, Integer> questProgress = new HashMap<>();
        Map<String, String> questVariables = new HashMap<>();
        List<String> completedIds = new ArrayList<>();
        List<String> claimedIds = new ArrayList<>();

        for (Quest q : allQuests) {
            questProgress.put(q.getId(), q.getCurrentProgress());
            if (q.getQuestVariable() != null) {
                questVariables.put(q.getId(), q.getQuestVariable());
            }
            if (q.isCompleted()) completedIds.add(q.getId());
            if (q.isClaimed()) claimedIds.add(q.getId());
        }

        UsersManager um = UsersManager.getInstance();
        um.setQuestProgressForCurrentUser(questProgress);
        um.setQuestVariablesForCurrentUser(questVariables);
        um.setCompletedQuestIdsForCurrentUser(completedIds);
        um.setClaimedQuestIdsForCurrentUser(claimedIds);
    }

    private void resetDailyQuests() {
        for (Quest q : allQuests) {
            if (q.isDailyReset()) {
                q.randomizeVariable();
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
        News news = new News("Quest completed: " + quest.getName() + "! Claim your rewards in Travel Log.");
        currentUser.getNewsManager().addNews(news);
        quest.setCompleted(true);
        saveProgress();
    }

    private void applyReward(Reward reward) {
        if (reward == null) return;
        UsersManager um = UsersManager.getInstance();
        switch (reward.getType()) {
            case COINS:
                um.addCoins(reward.getAmount());
                break;
            case GEMS:
                um.addGems(reward.getAmount());
                break;
            case SEED_PACKETS:
                PlantType pt = reward.getPlantType() != null ? reward.getPlantType() : PlantType.SUNFLOWER;
                um.addSeedPackets(pt, reward.getAmount());
                break;
            case UNLOCK_PLANT:
                if (reward.getPlantType() != null) {
                    um.unlockPlant(reward.getPlantType());
                } else {
                    for (PlantType type : PlantType.values()) {
                        if (!um.getLoggedInUser().getUserProgress().getUnlockedPlantsAndTheirLevels().containsKey(type)) {
                            um.unlockPlant(type);
                            break;
                        }
                    }
                }
                break;
            case UNLOCK_CHAPTER:
                if (reward.getChapterType() != null) um.unlockChapter(reward.getChapterType());
                break;
            case PLANT_FOOD:
                um.addPlantFood(reward.getAmount());
                break;
            case POTS:
                um.addPots(reward.getAmount());
                break;
        }
    }

    public String claimReward(String questId) {
        for (Quest q : allQuests) {
            if (q.getId().equals(questId)) {
                if (!q.isCompleted()) return "Quest not completed yet.";
                if (q.isClaimed()) return "Reward already claimed.";

                q.setClaimed(true);
                applyReward(q.getReward());

                UsersManager um = UsersManager.getInstance();
                if (q.getCategory() == QuestCategory.DAILY) {
                    um.incrementDailyQuestsCompleted();
                } else {
                    um.incrementNonDailyQuestsCompleted();
                }

                if (!q.isDailyReset()) {
                    q.randomizeVariable();
                    q.reset();
                }

                saveProgress();
                return null;
            }
        }
        return "Quest not found.";
    }

    public List<Quest> getActiveQuests() {
        List<Quest> activeQuests = allQuests.stream()
            .filter(q -> !q.isCompleted() && !q.isClaimed())
            .sorted(Comparator.comparing(Quest::getPriority))
            .collect(Collectors.toList());
        Collections.reverse(activeQuests);
        return activeQuests;
    }

    public List<Quest> getCompletedQuests() {
        List<Quest> completedQuests = allQuests.stream()
            .filter(q -> q.isCompleted() && !q.isClaimed())
            .sorted(Comparator.comparing(Quest::getPriority))
            .collect(Collectors.toList());
        Collections.reverse(completedQuests);
        return completedQuests;
    }

    public List<Quest> getQuestsByPage(QuestPage page) {
        List<Quest> pageQuests = allQuests.stream()
            .filter(q -> q.getPage() == page)
            .sorted(Comparator.comparing(Quest::getPriority))
            .collect(Collectors.toList());
        Collections.reverse(pageQuests);
        return pageQuests;
    }
}
