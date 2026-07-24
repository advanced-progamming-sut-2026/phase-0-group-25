package src.Menu;

import src.Enums.Command;
import src.Enums.MenuType;
import src.Model.Quests.Quest;
import src.Model.Quests.QuestManager;
import src.View.ViewInterfaces.BaseView;
import src.View.ViewInterfaces.TravelLogMenuView;

import java.util.regex.Matcher;

public class TravelLogMenu extends Menu {
    private final TravelLogMenuView travelLogMenuView;
    private String currentPage = "quests"; 

    public TravelLogMenu(TravelLogMenuView travelLogMenuView) {
        super(MenuType.Game);
        this.travelLogMenuView = travelLogMenuView;
    }

    @Override
    public void handleSpecificCommands(String input) {
        Matcher matcher;

        if ((matcher = getMatcher(input, Command.TravelLogPage)) != null) {
            currentPage = matcher.group(1);
            if (currentPage.equals("quests")) {
                showQuestsPage();
            } else if (currentPage.equals("minigames")) {
                travelLogMenuView.showMinigames();
            } else {
                getView().showError("Unknown page: " + currentPage);
            }
            return;
        }

        if ((matcher = getMatcher(input, Command.ShowQuests)) != null) {
            showQuestsPage();
            return;
        }

        if ((matcher = getMatcher(input, Command.ClaimQuestReward)) != null) {
            String questId = matcher.group(1);
            claimReward(questId);
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    private void showQuestsPage() {
        QuestManager qm = QuestManager.getInstance();
        
        travelLogMenuView.showQuests(qm.getActiveQuests(), qm.getCompletedQuests());
    }

    private void claimReward(String questId) {
        String error = QuestManager.getInstance().claimReward(questId);
        if (error == null) {
            travelLogMenuView.showRewardClaimed(questId);
            
            showQuestsPage();
        } else {
            getView().showError(error);
        }
    }

    @Override
    public BaseView getView() {
        return travelLogMenuView;
    }
}