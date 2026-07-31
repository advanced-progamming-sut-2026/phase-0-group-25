package src.Model.Quests;

import src.Enums.*;
import java.util.*;

public class QuestDefinitionLoader {

    public static List<Quest> loadQuests() {
        List<Quest> quests = new ArrayList<>();

        // 1. Daily Sun Collector (3 tiers)
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
            quests.add(q);
        }

        // 2. Chapter Hunter (one per chapter)
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
            quests.add(q);
        }

        // 3. Professional Plant Killer (any plant)
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
        quests.add(professionalKiller);

        // 4. Only Cactus
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
        quests.add(onlyCactus);

        // 5. Thrifty Farmer (0..5 losses)
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
            quests.add(q);
        }

        // 6. Master of Defense
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
        quests.add(masterDefense);

        // 7. Speed Demon
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
        quests.add(speedDemon);

        // 8. Professional Demolisher
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
        quests.add(demolisher);

        // 9. Symmetry
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
        quests.add(symmetry);

        // 10. Family Slaughter (generic)
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
        quests.add(familySlaughter);

        // 11. Blossom in Constraints (generic)
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
            // data[5] = Set<String> usedFamilies – we skip specific check for now
            return true;
        });
        quests.add(blossom);

        // 12. Night or Morning
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
        quests.add(nightMorning);

        // 13. Win Streak
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
        quests.add(winStreak);

        // 14. Almost Victory
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
        quests.add(almostVictory);

        // 15. No OCD
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
        quests.add(noOCD);

        // 16. Cloudy Day
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
        quests.add(cloudy);

        // 17. One Column Less (columns 1..9)
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
            quests.add(q);
        }

        // 18. Defenseless Row (rows 1..5)
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
            quests.add(q);
        }

        // 19. Defenseless Cross (n=1..5)
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
            quests.add(q);
        }

        // 20. Mower Time (tiers 10,20,30,40,50)
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
            quests.add(q);
        }

        return quests;
    }

    private static Quest createQuest(String id, String name, String description, QuestCategory category,
                                     QuestPriority priority, QuestEvent event, int required,
                                     List<Reward> rewards, Map<String, Object> conditions, boolean daily) {
        return new Quest(id, name, description, category, priority, event, required, rewards, conditions, daily);
    }
}