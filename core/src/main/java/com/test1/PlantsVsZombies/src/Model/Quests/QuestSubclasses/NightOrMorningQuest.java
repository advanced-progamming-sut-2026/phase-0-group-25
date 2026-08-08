package com.test1.PlantsVsZombies.src.Model.Quests.QuestSubclasses;//package src.Model.Quests.QuestSubclasses;
//
//import com.test1.PlantsVsZombies.src.Enums.*;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.Event;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.LevelWonEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.MowerTriggeredEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Events.SunCollectedEvent;
//import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
//import com.test1.PlantsVsZombies.src.Model.Quests.Reward;
//
//import java.util.List;
//import java.util.Map;
//public class NightOrMorningQuest extends Quest {
//    private final int required = 1;
//
//    public NightOrMorningQuest(String id, String name, String description,
//                               QuestCategory category, QuestPriority priority,
//                               List<Reward> rewards, Map<String, Object> conditions,
//                               boolean dailyReset, QuestPage page) {
//        super(id, name, description, category, priority, rewards, conditions, dailyReset, page);
//    }
//
//    @Override
//    public int getRequiredCount() { return required; }
//
//    @Override
//    public void check(Event event) {
//        if (!(event instanceof LevelWonEvent)) return;
//        LevelWonEvent e = (LevelWonEvent) event;
//        if (e.isOnlyMushrooms()) {
//            incrementProgress(1);
//        }
//    }
//}
