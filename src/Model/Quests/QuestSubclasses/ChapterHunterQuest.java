package src.Model.Quests.QuestSubclasses;
import src.Enums.*; import src.Model.Quests.Events.*; import src.Model.Quests.*; import java.util.Random;

public class ChapterHunterQuest extends Quest {
    private ChapterType targetChapter;
    private final int required = 50;

    public ChapterHunterQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg); randomizeVariable();
    }

    @Override public void randomizeVariable() {
        ChapterType[] chapters = ChapterType.values();
        this.targetChapter = chapters[new Random().nextInt(chapters.length)];
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Hunter: " + targetChapter.getName();
        this.description = "Defeat 50 zombies from the " + targetChapter.getName() + " chapter.";
        this.reward = new Reward(RewardType.SEED_PACKETS, 10, PlantType.SUNFLOWER);
    }

    @Override public int getRequiredCount() { return required; }

    @Override public void check(Event e) {
        if (e instanceof ZombieKilledEvent && ((ZombieKilledEvent) e).getChapter() == targetChapter) {
            incrementProgress(1);
        }
    }

    @Override public String getQuestVariable() { return targetChapter.name(); }
    @Override public void setQuestVariable(String v) { this.targetChapter = ChapterType.valueOf(v); updateDetails(); }
}