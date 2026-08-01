package src.Model.Quests.QuestSubclasses;
import src.Enums.*;
import src.Model.Quests.Events.*;
import src.Model.Quests.*;
import java.util.*;

public class DefenselessCrossQuest extends Quest {
    private int target;

    public DefenselessCrossQuest(String id, QuestCategory c, QuestPriority p, boolean dr, QuestPage pg) {
        super(id, c, p, dr, pg); randomizeVariable();
    }

    @Override public void randomizeVariable() {
        this.target = new Random().nextInt(5) + 1;
        updateDetails();
    }

    private void updateDetails() {
        this.name = "Defenseless Cross (" + target + ")";
        this.description = "Win a level keeping both column " + target + " and row " + target + " completely empty.";
        this.reward = new Reward(RewardType.GEMS, 25);
    }

    @Override public int getRequiredCount() { return 1; }
    @Override public void check(Event e) {
        if (e instanceof LevelWonEvent) {
            boolean[] emptyCols = ((LevelWonEvent) e).getEmptyColumns();
            boolean[] emptyRows = ((LevelWonEvent) e).getEmptyRows();
            if (emptyCols != null && emptyRows != null && emptyCols.length > target && emptyRows.length > target && emptyCols[target] && emptyRows[target]) {
                incrementProgress(1);
            }
        }
    }

    @Override public String getQuestVariable() { return String.valueOf(target); }
    @Override public void setQuestVariable(String v) { this.target = Integer.parseInt(v); updateDetails(); }
}