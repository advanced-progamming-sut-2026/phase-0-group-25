package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;//package src.Model.Quests.QuestSubclasses;
//
//import com.test1.PlantsVsZombies.src.Enums.*;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.SunCollectedEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.ZombieKilledEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
//import com.test1.PlantsVsZombies.src.Model.Quests.Reward;
//
//import java.util.List;
//import java.util.Map;
//
//
//public class FamilySlaughterQuest extends Quest {
//    private final int required = 10;
//
//    public FamilySlaughterQuest(String id, String name, String description,
//                                QuestCategory category, QuestPriority priority,
//                                List<Reward> rewards, Map<String, Object> conditions,
//                                boolean dailyReset, QuestPage page) {
//        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
//    }
//
//    @Override
//    public int getRequiredCount() { return required; }
//
//    @Override
//    public void check(Event event) {
//
//
//        if (event instanceof ZombieKilledEvent) {
//            incrementProgress(1);
//        }
//    }
//}
