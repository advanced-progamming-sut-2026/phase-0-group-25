package src.Model.Quests;

import src.Enums.*;
import src.Model.News.News;
import src.Model.User.User;
import src.Model.User.UserProgress;
import src.Model.User.UsersManager;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class QuestManager {
    private static QuestManager instance;
    private List<Quest> allQuests;
    private Map<QuestEvent, List<Quest>> eventListeners;
    private User currentUser;

    private QuestManager() {
        allQuests = new ArrayList<>();
        eventListeners = new HashMap<>();
        loadQuests();
        registerEventListeners();
        // Load progress from current user (will be called after login)
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

    // ---------- Load quest definitions ----------
    private void loadQuests() {
        // (1) Daily Sun Collector
        allQuests.add(createQuest(
                "daily_sun_collector",
                "Daily Sun Collector",
                "Collect {sun_amount} sun in a single day.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                QuestEvent.SUN_COLLECTED,
                3000, // base required, but we'll support multiple tiers via conditions
                List.of(new Reward(RewardType.COINS, 30)), // sun_amount/100 = 30 for 3000
                Map.of("tiers", List.of(3000, 4000, 5000)),
                true
        ));

        // (2) Chapter Hunter (one per chapter)
        for (ChapterType chapter : ChapterType.values()) {
            allQuests.add(createQuest(
                    "hunter_" + chapter.name().toLowerCase(),
                    "Hunter: " + chapter.getName(),
                    "Kill 50 zombies from " + chapter.getName() + ".",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    QuestEvent.ZOMBIE_KILLED,
                    50,
                    List.of(new Reward(RewardType.SEED_PACKETS, 10, PlantType.SUNFLOWER)), // placeholder seed
                    Map.of("chapter", chapter),
                    false
            ));
        }

        // (3) Professional Plant Killer (one per plant that can kill)
        // We'll create generic for now – will be specialized later.
        allQuests.add(createQuest(
                "professional_killer",
                "Professional Plant Killer",
                "Kill 10 zombies using only a specific plant.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.ZOMBIE_KILLED,
                10,
                List.of(new Reward(RewardType.UNLOCK_PLANT, 1, PlantType.PEASHOOTER)), // placeholder
                Map.of("plantType", "ANY"),
                true
        ));

        // (4) Only Cactus
        allQuests.add(createQuest(
                "only_cactus",
                "Only Cactus",
                "Kill 10 zombies using only Cactus.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.ZOMBIE_KILLED,
                10,
                List.of(new Reward(RewardType.GEMS, 20)),
                Map.of("plantType", "CACTUS"),
                true
        ));

        // (5) Thrifty Farmer (one per loss limit)
        for (int n = 0; n <= 5; n++) {
            allQuests.add(createQuest(
                    "thrifty_farmer_" + n,
                    "Thrifty Farmer (" + n + " losses)",
                    "Win a level losing no more than " + n + " plants.",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.SEED_PACKETS, 20 - n, PlantType.SUNFLOWER)),
                    Map.of("maxLoss", n),
                    false
            ));
        }

        // (6) Master of Defense
        allQuests.add(createQuest(
                "master_defense",
                "Master of Defense",
                "Complete a level with exactly 0 sun left.",
                QuestCategory.CHALLENGE,
                QuestPriority.CRITICAL,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.GEMS, 200)),
                Map.of("finalSun", 0),
                false
        ));

        // (7) Speed Demon
        allQuests.add(createQuest(
                "speed_demon",
                "Speed Demon",
                "Kill 10 zombies in less than 30 seconds after first wave.",
                QuestCategory.MAIN,
                QuestPriority.MEDIUM,
                QuestEvent.ZOMBIE_KILLED,
                10,
                List.of(new Reward(RewardType.COINS, 500)),
                Map.of("timeLimit", 30),
                false
        ));

        // (8) Professional Demolisher
        allQuests.add(createQuest(
                "professional_demolisher",
                "Professional Demolisher",
                "Use 3 explosive plants in one level.",
                QuestCategory.DAILY,
                QuestPriority.LOW,
                QuestEvent.EXPLOSIVE_USED,
                3,
                List.of(new Reward(RewardType.COINS, 100)),
                Map.of(),
                true
        ));

        // (9) Symmetry
        allQuests.add(createQuest(
                "symmetry",
                "Symmetry",
                "End the game with a symmetrical garden.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.SYMMETRY_CHECK,
                1,
                List.of(new Reward(RewardType.COINS, 500)),
                Map.of(),
                true
        ));

        // (10) Family Slaughter (one per family)
        // We'll define generic family placeholder – will be expanded later.
        allQuests.add(createQuest(
                "family_slaughter",
                "Family Slaughter",
                "Kill zombies using only plants of a specific family.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                QuestEvent.FAMILY_USED,
                10,
                List.of(new Reward(RewardType.COINS, 1000)),
                Map.of("family", "ANY"),
                true
        ));

        // (11) Blossom in Constraints (one per family)
        allQuests.add(createQuest(
                "blossom_constraints",
                "Blossom in Constraints",
                "Win without using any plant from a specific family.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.GEMS, 100)),
                Map.of("family", "ANY"),
                true
        ));

        // (12) Night or Morning
        allQuests.add(createQuest(
                "night_or_morning",
                "Night or Morning",
                "Win a day level using only night plants (mushrooms).",
                QuestCategory.CHALLENGE,
                QuestPriority.HIGH,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.GEMS, 20)),
                Map.of("useMushrooms", true),
                false
        ));

        // (13) Win Streak
        allQuests.add(createQuest(
                "win_streak",
                "Win Streak",
                "Win 5 levels in a row at highest difficulty.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                QuestEvent.LEVEL_WON,
                5,
                List.of(new Reward(RewardType.COINS, 5000)),
                Map.of("difficulty", 5),
                true
        ));

        // (14) Almost Victory
        allQuests.add(createQuest(
                "almost_victory",
                "Almost Victory",
                "Kill 10 zombies in the first column of a row without a mower.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                QuestEvent.ZOMBIE_KILLED,
                10,
                List.of(new Reward(RewardType.COINS, 300)),
                Map.of("column", 1, "noMower", true),
                true
        ));

        // (15) No OCD
        allQuests.add(createQuest(
                "no_ocd",
                "No OCD",
                "Win with no symmetry (except middle row).",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.COINS, 800)),
                Map.of(),
                true
        ));

        // (16) Cloudy Day
        allQuests.add(createQuest(
                "cloudy_day",
                "Cloudy Day",
                "Win using only 3 sun-producing plants.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.GEMS, 10)),
                Map.of("sunProducers", 3),
                true
        ));

        // (17) One Column Less (for each column)
        for (int col = 1; col <= 9; col++) {
            allQuests.add(createQuest(
                    "one_column_less_" + col,
                    "One Column Less (" + col + ")",
                    "Win without planting in column " + col + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 10)),
                    Map.of("emptyColumn", col),
                    true
            ));
        }

        // (18) Defenseless Row (for each row)
        for (int row = 1; row <= 5; row++) {
            allQuests.add(createQuest(
                    "defenseless_row_" + row,
                    "Defenseless Row (" + row + ")",
                    "Win without planting in row " + row + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 20)),
                    Map.of("emptyRow", row),
                    true
            ));
        }

        // (19) Defenseless Cross (for each min(row,col))
        for (int n = 1; n <= 5; n++) {
            allQuests.add(createQuest(
                    "defenseless_cross_" + n,
                    "Defenseless Cross (" + n + ")",
                    "Win with column and row " + n + " empty.",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 25)),
                    Map.of("emptyCross", n),
                    true
            ));
        }

        // (20) Mower Time (multiple tiers)
        for (int n : List.of(10, 20, 30, 40, 50)) {
            allQuests.add(createQuest(
                    "mower_time_" + n,
                    "Mower Time (" + n + ")",
                    "Kill at least " + n + " zombies with mowers.",
                    QuestCategory.CHALLENGE,
                    QuestPriority.MEDIUM,
                    QuestEvent.MOWER_TRIGGERED,
                    n,
                    List.of(new Reward(RewardType.GEMS, n)),
                    Map.of(),
                    false
            ));
        }
    }

    private Quest createQuest(String id, String name, String description, QuestCategory category,
                              QuestPriority priority, QuestEvent event, int required,
                              List<Reward> rewards, Map<String, Object> conditions, boolean daily) {
        Quest q = new Quest(id, name, description, category, priority, event, required, rewards, conditions, daily);
        // Set condition checker for complex conditions (can be customized later)
        q.setConditionChecker((data) -> {
            // Default: always true – we'll implement specific checks in the event handlers
            return true;
        });
        return q;
    }

    // ---------- Event listeners registration ----------
    private void registerEventListeners() {
        for (Quest q : allQuests) {
            eventListeners.computeIfAbsent(q.getTriggerEvent(), k -> new ArrayList<>()).add(q);
        }
    }

    // ---------- Load/Save progress ----------
    public void loadProgress() {
        if (currentUser == null) return;
        UserProgress progress = currentUser.getUserProgress();
        Map<String, Integer> questProgress = progress.getQuestProgress();
        List<String> completedIds = progress.getCompletedQuestIds();
        List<String> claimedIds = progress.getClaimedQuestIds();
        LocalDate lastDailyReset = progress.getLastDailyReset();

        // Reset daily quests if needed
        LocalDate today = LocalDate.now();
        if (lastDailyReset == null || !lastDailyReset.equals(today)) {
            resetDailyQuests();
            progress.setLastDailyReset(today);
            UsersManager.getInstance().updateUser();
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
        progress.setQuestProgress(questProgress);
        progress.setCompletedQuestIds(completedIds);
        progress.setClaimedQuestIds(claimedIds);
        UsersManager.getInstance().updateUser();
    }

    private void resetDailyQuests() {
        for (Quest q : allQuests) {
            if (q.isDailyReset()) {
                q.reset();
            }
        }
    }

    // ---------- Event notification (to be called from GamePlay later) ----------
    public void notifyEvent(QuestEvent event, Object... data) {
        if (currentUser == null) {
            currentUser = UsersManager.getInstance().getLoggedInUser();
            if (currentUser == null) return;
            loadProgress();
        }

        List<Quest> listeners = eventListeners.get(event);
        if (listeners == null) return;

        // Check daily reset first
        LocalDate today = LocalDate.now();
        if (currentUser.getUserProgress().getLastDailyReset() == null ||
                !currentUser.getUserProgress().getLastDailyReset().equals(today)) {
            resetDailyQuests();
            currentUser.getUserProgress().setLastDailyReset(today);
            UsersManager.getInstance().updateUser();
        }

        for (Quest quest : listeners) {
            if (quest.isCompleted() || quest.isClaimed()) continue;

            // Check conditions (if any)
            if (!quest.meetsConditions(data)) continue;

            // Update progress based on event
            updateQuestProgress(quest, event, data);
            if (quest.checkCompletion()) {
                onQuestCompleted(quest);
            }
        }
        saveProgress();
    }

    private void updateQuestProgress(Quest quest, QuestEvent event, Object... data) {
        // Generic progress update – can be overridden per quest type
        switch (event) {
            case ZOMBIE_KILLED:
                // data[0] = ZombieType, data[1] = plantType (if applicable)
                quest.incrementProgress(1);
                break;
            case SUN_COLLECTED:
                // data[0] = amount
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
            case PLANT_PLANTED:
                // data[0] = plantName, data[1] = position
                quest.incrementProgress(1);
                break;
            case EXPLOSIVE_USED:
                quest.incrementProgress(1);
                break;
            case MOWER_TRIGGERED:
                // data[0] = count killed
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
            case LEVEL_WON:
                quest.incrementProgress(1);
                break;
            case SYMMETRY_CHECK:
                quest.incrementProgress(1);
                break;
            case FAMILY_USED:
                quest.incrementProgress(1);
                break;
            default:
                // For other events, increment by 1
                quest.incrementProgress(1);
                break;
        }
    }

    private void onQuestCompleted(Quest quest) {
        // Apply rewards
        applyRewards(quest.getRewards());

        // Send news notification
        News news = new News("🎉 Quest completed: " + quest.getName() + "! Claim your rewards in Travel Log.");
        currentUser.getNewsManager().addNews(news);

        // Mark as completed (will be saved later)
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
        Quest quest = allQuests.stream().filter(q -> q.getId().equals(questId)).findFirst().orElse(null);
        if (quest == null) return "Quest not found.";
        if (!quest.isCompleted()) return "Quest not completed yet.";
        if (quest.isClaimed()) return "Reward already claimed.";

        // Apply rewards again (in case they weren't applied on completion)
        applyRewards(quest.getRewards());
        quest.setClaimed(true);
        saveProgress();
        return null;
    }

    // ---------- Display methods for TravelLogMenu ----------
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
}