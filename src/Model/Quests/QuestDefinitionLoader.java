package src.Model.Quests;

import src.Enums.*;
import src.Model.Quests.QuestSubclasses.*;
import java.util.*;

public class QuestDefinitionLoader {
    public static List<Quest> loadQuests() {
        List<Quest> quests = new ArrayList<>();

        quests.add(new DailySunCollectorQuest("daily_sun_collector", QuestCategory.DAILY, QuestPriority.MEDIUM, true, QuestPage.SUN_COLLECTOR));
        quests.add(new ChapterHunterQuest("chapter_hunter", QuestCategory.MAIN, QuestPriority.HIGH, false, QuestPage.ZOMBIE_SLAYER));
        quests.add(new ThriftyFarmerQuest("thrifty_farmer", QuestCategory.MAIN, QuestPriority.HIGH, false, QuestPage.GARDENER));
        quests.add(new MasterOfDefenseQuest("master_defense", QuestCategory.CHALLENGE, QuestPriority.CRITICAL, false, QuestPage.GARDENER));
        quests.add(new SpeedDemonQuest("speed_demon", QuestCategory.MAIN, QuestPriority.MEDIUM, false, QuestPage.ZOMBIE_SLAYER));
        quests.add(new ProfessionalDemolisherQuest("professional_demolisher", QuestCategory.DAILY, QuestPriority.LOW, true, QuestPage.CHALLENGES));
        quests.add(new WinStreakQuest("win_streak", QuestCategory.DAILY, QuestPriority.MEDIUM, true, QuestPage.GARDENER));
        quests.add(new CloudyDayQuest("cloudy_day", QuestCategory.DAILY, QuestPriority.HIGH, true, QuestPage.GARDENER));
        quests.add(new OneColumnLessQuest("one_column_less", QuestCategory.DAILY, QuestPriority.HIGH, true, QuestPage.GARDENER));
        quests.add(new DefenselessRowQuest("defenseless_row", QuestCategory.DAILY, QuestPriority.HIGH, true, QuestPage.GARDENER));
        quests.add(new DefenselessCrossQuest("defenseless_cross", QuestCategory.DAILY, QuestPriority.HIGH, true, QuestPage.GARDENER));
        quests.add(new MowerTimeQuest("mower_time", QuestCategory.CHALLENGE, QuestPriority.MEDIUM, false, QuestPage.ZOMBIE_SLAYER));

        return quests;
    }
}