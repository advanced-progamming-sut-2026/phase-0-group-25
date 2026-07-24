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


    private void loadQuests() {

        int[] sunTiers = {3000, 4000, 5000};
        for (int tier : sunTiers) {
            String id = "daily_sun_collector_" + tier;
            int rewardCoins = tier / 100;
            Quest q = createQuest(
                    id,
                    "Daily Sun Collector (" + tier + " sun)",
                    "Collect " + tier + " sun in a single day.",
                    QuestCategory.DAILY,
                    QuestPriority.MEDIUM,
                    QuestEvent.SUN_COLLECTED,
                    tier,
                    List.of(new Reward(RewardType.COINS, rewardCoins)),
                    Map.of(),
                    true
            );
            q.setConditionChecker((data) -> {
                if (data.length == 0 || !(data[0] instanceof Integer)) return false;
                int collected = (int) data[0];
                return collected >= tier;
            });
            allQuests.add(q);
        }


        for (ChapterType chapter : ChapterType.values()) {
            String id = "hunter_" + chapter.name().toLowerCase();
            Quest q = createQuest(
                    id,
                    "Hunter: " + chapter.getName(),
                    "Kill 50 zombies from " + chapter.getName() + ".",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    QuestEvent.ZOMBIE_KILLED,
                    50,
                    List.of(new Reward(RewardType.SEED_PACKETS, 10, PlantType.SUNFLOWER)),
                    Map.of("chapter", chapter),
                    false
            );
            q.setConditionChecker((data) -> {
                if (data.length < 1) return false;
                Object zombieChapter = data[0];
                return chapter.equals(zombieChapter);
            });
            allQuests.add(q);
        }


        Quest professionalKiller = createQuest(
                "professional_killer",
                "Professional Plant Killer",
                "Kill 10 zombies using any plant.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.ZOMBIE_KILLED,
                10,
                List.of(new Reward(RewardType.UNLOCK_PLANT, 1, PlantType.PEASHOOTER)),
                Map.of("plantType", "ANY"),
                true
        );
        professionalKiller.setConditionChecker((data) -> true);
        allQuests.add(professionalKiller);


        Quest onlyCactus = createQuest(
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
        );
        onlyCactus.setConditionChecker((data) -> {
            if (data.length < 2) return false;
            String plant = (String) data[1];
            return "CACTUS".equalsIgnoreCase(plant);
        });
        allQuests.add(onlyCactus);


        for (int n = 0; n <= 5; n++) {
            int finalN = n;
            String id = "thrifty_farmer_" + n;
            Quest q = createQuest(
                    id,
                    "Thrifty Farmer (" + n + " losses)",
                    "Win a level losing no more than " + n + " plants.",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.SEED_PACKETS, 20 - n, PlantType.SUNFLOWER)),
                    Map.of("maxLoss", n),
                    false
            );
            q.setConditionChecker((data) -> {
                if (data.length < 3) return false;

                int lost = (int) data[2];
                return lost <= finalN;
            });
            allQuests.add(q);
        }


        Quest masterDefense = createQuest(
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
        );
        masterDefense.setConditionChecker((data) -> {
            if (data.length < 4) return false;
            int sun = (int) data[3];
            return sun == 0;
        });
        allQuests.add(masterDefense);


        Quest speedDemon = createQuest(
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
        );
        speedDemon.setConditionChecker((data) -> {

            if (data.length < 1) return false;
            double time = (double) data[0];
            return time < 30;
        });
        allQuests.add(speedDemon);


        Quest demolisher = createQuest(
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
        );
        demolisher.setConditionChecker((data) -> true);
        allQuests.add(demolisher);


        Quest symmetry = createQuest(
                "symmetry",
                "Symmetry",
                "End the game with a symmetrical garden.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                QuestEvent.LEVEL_WON,
                1,
                List.of(new Reward(RewardType.COINS, 500)),
                Map.of(),
                true
        );
        symmetry.setConditionChecker((data) -> {
            if (data.length < 5) return false;
            boolean isSymmetric = (boolean) data[4];
            return isSymmetric;
        });
        allQuests.add(symmetry);


        Quest familySlaughter = createQuest(
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
        );
        familySlaughter.setConditionChecker((data) -> true);
        allQuests.add(familySlaughter);


        Quest blossom = createQuest(
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
        );
        blossom.setConditionChecker((data) -> {
            if (data.length < 6) return false;

            Set<String> usedFamilies = (Set<String>) data[5];


            return true;
        });
        allQuests.add(blossom);


        Quest nightMorning = createQuest(
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
        );
        nightMorning.setConditionChecker((data) -> {
            if (data.length < 7) return false;

            boolean onlyMushrooms = (boolean) data[6];
            return onlyMushrooms;
        });
        allQuests.add(nightMorning);


        Quest winStreak = createQuest(
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
        );
        winStreak.setConditionChecker((data) -> {
            if (data.length < 8) return false;
            int difficulty = (int) data[7];
            return difficulty == 5;
        });
        allQuests.add(winStreak);


        Quest almostVictory = createQuest(
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
        );
        almostVictory.setConditionChecker((data) -> {
            if (data.length < 3) return false;
            int column = (int) data[2];
            boolean noMower = (boolean) data[3];
            return column == 1 && noMower;
        });
        allQuests.add(almostVictory);


        Quest noOCD = createQuest(
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
        );
        noOCD.setConditionChecker((data) -> {
            if (data.length < 5) return false;
            boolean isSymmetric = (boolean) data[4];

            return !isSymmetric;
        });
        allQuests.add(noOCD);


        Quest cloudy = createQuest(
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
        );
        cloudy.setConditionChecker((data) -> {
            if (data.length < 8) return false;
            int sunProducers = (int) data[8];
            return sunProducers == 3;
        });
        allQuests.add(cloudy);


        for (int col = 1; col <= 9; col++) {
            int finalCol = col;
            String id = "one_column_less_" + col;
            Quest q = createQuest(
                    id,
                    "One Column Less (" + col + ")",
                    "Win without planting in column " + col + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 10)),
                    Map.of("emptyColumn", col),
                    true
            );
            q.setConditionChecker((data) -> {
                if (data.length < 9) return false;
                boolean[] emptyColumns = (boolean[]) data[9];
                return emptyColumns[finalCol];
            });
            allQuests.add(q);
        }


        for (int row = 1; row <= 5; row++) {
            int finalRow = row;
            String id = "defenseless_row_" + row;
            Quest q = createQuest(
                    id,
                    "Defenseless Row (" + row + ")",
                    "Win without planting in row " + row + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 20)),
                    Map.of("emptyRow", row),
                    true
            );
            q.setConditionChecker((data) -> {
                if (data.length < 10) return false;
                boolean[] emptyRows = (boolean[]) data[10];
                return emptyRows[finalRow];
            });
            allQuests.add(q);
        }


        for (int n = 1; n <= 5; n++) {
            int finalN = n;
            String id = "defenseless_cross_" + n;
            Quest q = createQuest(
                    id,
                    "Defenseless Cross (" + n + ")",
                    "Win with column and row " + n + " empty.",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    QuestEvent.LEVEL_WON,
                    1,
                    List.of(new Reward(RewardType.GEMS, 25)),
                    Map.of("emptyCross", n),
                    true
            );
            q.setConditionChecker((data) -> {
                if (data.length < 11) return false;
                boolean[] emptyCols = (boolean[]) data[9];
                boolean[] emptyRows = (boolean[]) data[10];
                return emptyCols[finalN] && emptyRows[finalN];
            });
            allQuests.add(q);
        }


        for (int n : List.of(10, 20, 30, 40, 50)) {
            int finalN = n;
            String id = "mower_time_" + n;
            Quest q = createQuest(
                    id,
                    "Mower Time (" + n + ")",
                    "Kill at least " + n + " zombies with mowers.",
                    QuestCategory.CHALLENGE,
                    QuestPriority.MEDIUM,
                    QuestEvent.MOWER_TRIGGERED,
                    n,
                    List.of(new Reward(RewardType.GEMS, n)),
                    Map.of(),
                    false
            );
            q.setConditionChecker((data) -> {
                if (data.length < 1) return false;
                int killed = (int) data[0];
                return killed >= finalN;
            });
            allQuests.add(q);
        }
    }

    private Quest createQuest(String id, String name, String description, QuestCategory category,
                              QuestPriority priority, QuestEvent event, int required,
                              List<Reward> rewards, Map<String, Object> conditions, boolean daily) {
        return new Quest(id, name, description, category, priority, event, required, rewards, conditions, daily);
    }


    private void registerEventListeners() {
        for (Quest q : allQuests) {
            eventListeners.computeIfAbsent(q.getTriggerEvent(), k -> new ArrayList<>()).add(q);
        }
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


    public void notifyEvent(QuestEvent event, Object... data) {
        if (currentUser == null) {
            currentUser = UsersManager.getInstance().getLoggedInUser();
            if (currentUser == null) return;
            loadProgress();
        }

        List<Quest> listeners = eventListeners.get(event);
        if (listeners == null) return;

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

                quest.incrementProgress(1);
                break;
            case SUN_COLLECTED:
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
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
            case MOWER_TRIGGERED:
                if (data.length > 0 && data[0] instanceof Integer) {
                    quest.incrementProgress((int) data[0]);
                }
                break;
            case COLUMN_EMPTY:
            case ROW_EMPTY:
            case CROSS_EMPTY:

                break;
            default:
                quest.incrementProgress(1);
                break;
        }
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
        Quest quest = allQuests.stream().filter(q -> q.getId().equals(questId)).findFirst().orElse(null);
        if (quest == null) return "Quest not found.";
        if (!quest.isCompleted()) return "Quest not completed yet.";
        if (quest.isClaimed()) return "Reward already claimed.";


        quest.setClaimed(true);
        saveProgress();
        return null;
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
}