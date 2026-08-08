package com.test1.PlantsVsZombies.src.Menu;

import com.test1.PlantsVsZombies.src.Enums.*;
import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame.VaseBreaker;
import com.test1.PlantsVsZombies.src.Model.MiniGames.WallnutBowlingGame.WalnutBowling;
import com.test1.PlantsVsZombies.src.Model.Quests.Quest;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.BaseView;
import com.test1.PlantsVsZombies.src.View.ViewInterfaces.TravelLogMenuView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

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
            String pageName = matcher.group(1);
            currentPage = pageName;
            QuestPage questPage = QuestPage.fromCommandName(pageName);
            if (questPage != null) {
                showQuestsPage(questPage);
            } else if (pageName.equals("minigames")) {
                travelLogMenuView.showMinigames();
            } else if (pageName.equals("quests")) {
                showQuestsPage(null);
            } else {
                getView().showError("Unknown page: " + pageName);
            }
            return;
        }

        if ((matcher = getMatcher(input, Command.EnterMiniGame)) != null) {
            String miniGameName = matcher.group(1).trim();
            startMiniGame(miniGameName);
            return;
        }


        if ((matcher = getMatcher(input, Command.ClaimQuestReward)) != null) {
            String questId = matcher.group(1);
            claimReward(questId);
            return;
        }

        getView().showError("Invalid command format for this menu state.");
    }

    private void startMiniGame(String miniGameName) {

        MiniGameType type = MiniGameType.fromDisplayName(miniGameName);
        if (type == null) {
            getView().showError("Unknown mini-game: " + miniGameName + ". Available: Vasebreaker, Walnut Bowling, I Zombie");
            return;
        }


        User currentUser = UsersManager.getInstance().getLoggedInUser();
        if (currentUser == null || currentUser.getUserProgress() == null) {
            getView().showError("No logged in user found.");
            return;
        }


        GameMenu gameMenu = MenuManager.getInstance().getGameMenu();
        ArrayList<String> selectedPlants = gameMenu.getPlantsStr();


        HashSet<String> boostedSet = new HashSet<>(gameMenu.getBoostedPlants());


        ArrayList<String> zombies = new ArrayList<>();


        int level = currentUser.getUserProgress().getMiniGameLevel(type);

        int difficulty = currentUser.getUserProgress().getGameDifficulty();


        GamePlay gamePlay = null;

        switch (type) {
            case VASEBREAKER:
                gamePlay = new VaseBreaker(ChapterType.MINI_GAME, level, difficulty, currentUser,
                        selectedPlants, zombies, boostedSet);
                break;
            case WALNUT_BOWLING:
                zombies.add(ZombieType.DEFAULT.getName());
                zombies.add(ZombieType.CONE_HEAD.name());
                zombies.add(ZombieType.BUCKET_HEAD.name());
                gamePlay = new WalnutBowling(ChapterType.MINI_GAME, level, difficulty, currentUser,
                        selectedPlants, zombies, boostedSet);
                break;
            case I_ZOMBIE:
                gamePlay = new IZombie(ChapterType.MINI_GAME, level, difficulty, currentUser,
                        selectedPlants, zombies, boostedSet);
                break;
            default:
                getView().showError("Unhandled mini-game type.");
                return;
        }

        if (gamePlay == null) {
            getView().showError("Failed to initialize mini-game.");
            return;
        }

        GamePlayMenu.setGamePlay(gamePlay);
        travelLogMenuView.showMiniGameLaunched(type.getDisplayName());
        MenuManager.getInstance().changeMenu(MenuType.GamePlay);
    }

    private void showQuestsPage(QuestPage page) {
        QuestManager qm = QuestManager.getInstance();
        List<Quest> active, completed;
        if (page == null) {
            active = qm.getActiveQuests();
            completed = qm.getCompletedQuests();
        } else {
            active = qm.getQuestsByPage(page).stream()
                    .filter(q -> !q.isCompleted() && !q.isClaimed())
                    .collect(Collectors.toList());
            completed = qm.getQuestsByPage(page).stream()
                    .filter(q -> q.isCompleted() && !q.isClaimed())
                    .collect(Collectors.toList());
        }

        travelLogMenuView.showQuests(active, completed, page);
    }

    private void claimReward(String questId) {
        String error = QuestManager.getInstance().claimReward(questId);
        if (error == null) {
            travelLogMenuView.showRewardClaimed(questId);
        } else {
            getView().showError(error);
        }
    }

    @Override
    public BaseView getView() {
        return travelLogMenuView;
    }
}
