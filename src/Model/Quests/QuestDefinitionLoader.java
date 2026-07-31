package src.Model.Quests;

import src.Enums.*;
import src.Model.Quests.QuestSubclasses.*;

import java.util.*;

public class QuestDefinitionLoader {

    public static List<Quest> loadQuests() {
        List<Quest> quests = new ArrayList<>();

        
        int[] sunTiers = {3000, 4000, 5000};
        for (int tier : sunTiers) {
            String id = "daily_sun_collector_" + tier;
            int rewardCoins = tier / 100;
            quests.add(new DailySunCollectorQuest(
                    id,
                    "Daily Sun Collector (" + tier + " sun)",
                    "Collect " + tier + " sun in a single day.",
                    QuestCategory.DAILY,
                    QuestPriority.MEDIUM,
                    List.of(new Reward(RewardType.COINS, rewardCoins)),
                    Map.of(),
                    true,
                    QuestPage.SUN_COLLECTOR,
                    tier
            ));
        }

        
        for (ChapterType chapter : ChapterType.values()) {
            String id = "hunter_" + chapter.name().toLowerCase();
            quests.add(new ChapterHunterQuest(
                    id,
                    "Hunter: " + chapter.getName(),
                    "Kill 50 zombies from " + chapter.getName() + ".",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    List.of(new Reward(RewardType.SEED_PACKETS, 10, PlantType.SUNFLOWER)),
                    Map.of("chapter", chapter),
                    false,
                    QuestPage.ZOMBIE_SLAYER,
                    chapter
            ));
        }

        
        quests.add(new ProfessionalPlantKillerQuest(
                "professional_killer",
                "Professional Plant Killer",
                "Kill 10 zombies using any plant.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.UNLOCK_PLANT, 1, PlantType.PEASHOOTER)),
                Map.of("plantType", "ANY"),
                true,
                QuestPage.ZOMBIE_SLAYER
        ));

        
        quests.add(new OnlyCactusQuest(
                "only_cactus",
                "Only Cactus",
                "Kill 10 zombies using only Cactus.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.GEMS, 20)),
                Map.of("plantType", "CACTUS"),
                true,
                QuestPage.ZOMBIE_SLAYER
        ));

        
        for (int n = 0; n <= 5; n++) {
            String id = "thrifty_farmer_" + n;
            quests.add(new ThriftyFarmerQuest(
                    id,
                    "Thrifty Farmer (" + n + " losses)",
                    "Win a level losing no more than " + n + " plants.",
                    QuestCategory.MAIN,
                    QuestPriority.HIGH,
                    List.of(new Reward(RewardType.SEED_PACKETS, 20 - n, PlantType.SUNFLOWER)),
                    Map.of("maxLoss", n),
                    false,
                    QuestPage.GARDENER,
                    n
            ));
        }

        
        quests.add(new MasterOfDefenseQuest(
                "master_defense",
                "Master of Defense",
                "Complete a level with exactly 0 sun left.",
                QuestCategory.CHALLENGE,
                QuestPriority.CRITICAL,
                List.of(new Reward(RewardType.GEMS, 200)),
                Map.of("finalSun", 0),
                false,
                QuestPage.GARDENER
        ));

        
        quests.add(new SpeedDemonQuest(
                "speed_demon",
                "Speed Demon",
                "Kill 10 zombies in less than 30 seconds after first wave.",
                QuestCategory.MAIN,
                QuestPriority.MEDIUM,
                List.of(new Reward(RewardType.COINS, 500)),
                Map.of("timeLimit", 30),
                false,
                QuestPage.ZOMBIE_SLAYER
        ));

        
        quests.add(new ProfessionalDemolisherQuest(
                "professional_demolisher",
                "Professional Demolisher",
                "Use 3 explosive plants in one level.",
                QuestCategory.DAILY,
                QuestPriority.LOW,
                List.of(new Reward(RewardType.COINS, 100)),
                Map.of(),
                true,
                QuestPage.CHALLENGES
        ));

        
        quests.add(new SymmetryQuest(
                "symmetry",
                "Symmetry",
                "End the game with a symmetrical garden.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.COINS, 500)),
                Map.of(),
                true,
                QuestPage.GARDENER
        ));

        
        quests.add(new FamilySlaughterQuest(
                "family_slaughter",
                "Family Slaughter",
                "Kill zombies using only plants of a specific family.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                List.of(new Reward(RewardType.COINS, 1000)),
                Map.of("family", "ANY"),
                true,
                QuestPage.CHALLENGES
        ));

        
        quests.add(new BlossomInConstraintsQuest(
                "blossom_constraints",
                "Blossom in Constraints",
                "Win without using any plant from a specific family.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.GEMS, 100)),
                Map.of("family", "ANY"),
                true,
                QuestPage.CHALLENGES
        ));

        
        quests.add(new NightOrMorningQuest(
                "night_or_morning",
                "Night or Morning",
                "Win a day level using only night plants (mushrooms).",
                QuestCategory.CHALLENGE,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.GEMS, 20)),
                Map.of("useMushrooms", true),
                false,
                QuestPage.GARDENER
        ));

        
        quests.add(new WinStreakQuest(
                "win_streak",
                "Win Streak",
                "Win 5 levels in a row at highest difficulty.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                List.of(new Reward(RewardType.COINS, 5000)),
                Map.of("difficulty", 5),
                true,
                QuestPage.GARDENER
        ));

        
        quests.add(new AlmostVictoryQuest(
                "almost_victory",
                "Almost Victory",
                "Kill 10 zombies in the first column of a row without a mower.",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                List.of(new Reward(RewardType.COINS, 300)),
                Map.of("column", 1, "noMower", true),
                true,
                QuestPage.ZOMBIE_SLAYER
        ));

        
        quests.add(new NoOCDQuest(
                "no_ocd",
                "No OCD",
                "Win with no symmetry (except middle row).",
                QuestCategory.DAILY,
                QuestPriority.MEDIUM,
                List.of(new Reward(RewardType.COINS, 800)),
                Map.of(),
                true,
                QuestPage.GARDENER
        ));

        
        quests.add(new CloudyDayQuest(
                "cloudy_day",
                "Cloudy Day",
                "Win using only 3 sun-producing plants.",
                QuestCategory.DAILY,
                QuestPriority.HIGH,
                List.of(new Reward(RewardType.GEMS, 10)),
                Map.of("sunProducers", 3),
                true,
                QuestPage.GARDENER
        ));

        
        for (int col = 1; col <= 9; col++) {
            String id = "one_column_less_" + col;
            quests.add(new OneColumnLessQuest(
                    id,
                    "One Column Less (" + col + ")",
                    "Win without planting in column " + col + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    List.of(new Reward(RewardType.GEMS, 10)),
                    Map.of("emptyColumn", col),
                    true,
                    QuestPage.GARDENER,
                    col
            ));
        }

        
        for (int row = 1; row <= 5; row++) {
            String id = "defenseless_row_" + row;
            quests.add(new DefenselessRowQuest(
                    id,
                    "Defenseless Row (" + row + ")",
                    "Win without planting in row " + row + ".",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    List.of(new Reward(RewardType.GEMS, 20)),
                    Map.of("emptyRow", row),
                    true,
                    QuestPage.GARDENER,
                    row
            ));
        }

        
        for (int n = 1; n <= 5; n++) {
            String id = "defenseless_cross_" + n;
            quests.add(new DefenselessCrossQuest(
                    id,
                    "Defenseless Cross (" + n + ")",
                    "Win with column and row " + n + " empty.",
                    QuestCategory.DAILY,
                    QuestPriority.HIGH,
                    List.of(new Reward(RewardType.GEMS, 25)),
                    Map.of("emptyCross", n),
                    true,
                    QuestPage.GARDENER,
                    n
            ));
        }

        
        for (int n : List.of(10, 20, 30, 40, 50)) {
            String id = "mower_time_" + n;
            quests.add(new MowerTimeQuest(
                    id,
                    "Mower Time (" + n + ")",
                    "Kill at least " + n + " zombies with mowers.",
                    QuestCategory.CHALLENGE,
                    QuestPriority.MEDIUM,
                    List.of(new Reward(RewardType.GEMS, n)),
                    Map.of(),
                    false,
                    QuestPage.ZOMBIE_SLAYER,
                    n
            ));
        }

        return quests;
    }
}