//package src.Model.Quests.QuestSubclasses;
//import src.Enums.*;
//import src.Model.Quests.Events.*;
//import src.Model.Quests.*;
//import java.util.*;
//
//public class ProfessionalPlantKillerQuest extends Quest {
//    private PlantType targetPlant;
//    private final int required = 10;
//
//    public ProfessionalPlantKillerQuest(String id, QuestCategory category, QuestPriority priority, boolean dailyReset, QuestPage page) {
//        super(id, category, priority, dailyReset, page);
//        randomizeVariable();
//    }
//
//    @Override
//    public void randomizeVariable() {
//        PlantType[] attackers = {PlantType.PEASHOOTER, PlantType.REPEATER, PlantType.CABBAGE_PULT, PlantType.MELON_PULT, PlantType.CACTUS};
//        this.targetPlant = attackers[new Random().nextInt(attackers.length)];
//        this.name = "Professional Plant Killer";
//        this.description = "Kill 10 zombies using only " + targetPlant.getName() + ".";
//        this.reward = new Reward(RewardType.UNLOCK_PLANT, 1);
//    }
//
//    @Override public int getRequiredCount() { return required; }
//    @Override public void check(Event event) {
//        if (event instanceof ZombieKilledEvent && targetPlant.getName().equalsIgnoreCase(((ZombieKilledEvent) event).getKillerPlantName())) {
//            incrementProgress(1);
//        }
//    }
//}