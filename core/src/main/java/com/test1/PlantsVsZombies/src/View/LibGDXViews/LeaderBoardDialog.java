package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.test1.PlantsVsZombies.src.Enums.SortColumn;
import com.test1.PlantsVsZombies.src.Menu.LeaderBoardMenu;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UserProgress;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import pvz.skin.BorderedTable;

import java.util.List;

public class LeaderBoardDialog extends BorderedTable {

    private final Skin skin;
    private final Runnable onClose;

    private SortColumn selectedColumn = SortColumn.MINIGAMES;
    private boolean isAscending = false;

    private Table rowsTable;

    public LeaderBoardDialog(Skin skin, Runnable onClose) {
        super();
        this.skin = skin;
        this.onClose = onClose;

        this.pad(30);
        this.setSize(780, 560);

        buildUI();
    }

    private void buildUI() {
        this.clearChildren();


        Label title = new Label("LEADERBOARD", skin, "big");
        title.setColor(Color.BLACK);
        title.setFontScale(0.8f);
        title.setAlignment(Align.center);

        this.add(title).center().padBottom(15).row();


        Table controlsTable = new Table();
        controlsTable.left();

        Label sortLabel = new Label("Sort by: ", skin, "big");
        sortLabel.setFontScale(0.65f);
        sortLabel.setColor(Color.BLACK);
        controlsTable.add(sortLabel).padRight(10);

        CheckBox miniGamesCb = new CheckBox(" Mini-Games", skin);
        CheckBox dailyCb = new CheckBox(" Daily Quests", skin);
        CheckBox nonDailyCb = new CheckBox(" Non-Daily Quests", skin);

        miniGamesCb.getLabel().setColor(Color.BLACK);
        dailyCb.getLabel().setColor(Color.BLACK);
        nonDailyCb.getLabel().setColor(Color.BLACK);





        ButtonGroup<CheckBox> sortGroup = new ButtonGroup<>(miniGamesCb, dailyCb, nonDailyCb);
        sortGroup.setMinCheckCount(1);
        sortGroup.setMaxCheckCount(1);

        if (selectedColumn == SortColumn.MINIGAMES) miniGamesCb.setChecked(true);
        else if (selectedColumn == SortColumn.DAILY) dailyCb.setChecked(true);
        else if (selectedColumn == SortColumn.NONDAILY) nonDailyCb.setChecked(true);

        ClickListener sortListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (miniGamesCb.isChecked()) selectedColumn = SortColumn.MINIGAMES;
                else if (dailyCb.isChecked()) selectedColumn = SortColumn.DAILY;
                else if (nonDailyCb.isChecked()) selectedColumn = SortColumn.NONDAILY;
                refreshRows();
            }
        };

        miniGamesCb.addListener(sortListener);
        dailyCb.addListener(sortListener);
        nonDailyCb.addListener(sortListener);

        controlsTable.add(miniGamesCb).padRight(12);
        controlsTable.add(dailyCb).padRight(12);
        controlsTable.add(nonDailyCb).padRight(20);


        CheckBox descCb = new CheckBox(" Desc", skin);
        CheckBox ascCb = new CheckBox(" Asc", skin);
        descCb.getLabel().setColor(Color.BLACK);
        ascCb.getLabel().setColor(Color.BLACK);

        ButtonGroup<CheckBox> orderGroup = new ButtonGroup<>(descCb, ascCb);
        orderGroup.setMinCheckCount(1);
        orderGroup.setMaxCheckCount(1);
        if (isAscending) ascCb.setChecked(true);
        else descCb.setChecked(true);

        ClickListener orderListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isAscending = ascCb.isChecked();
                refreshRows();
            }
        };
        descCb.addListener(orderListener);
        ascCb.addListener(orderListener);

        controlsTable.add(descCb).padRight(8);
        controlsTable.add(ascCb);

        this.add(controlsTable).fillX().padBottom(12).row();


        Table tableHeader = new Table();
        tableHeader.setBackground(skin.getDrawable("image_ui_quests_panel_edge_to_edge_ten"));

        addHeaderCell(tableHeader, "#", 45);
        addHeaderCell(tableHeader, "Username", 155);
        addHeaderCell(tableHeader, "Last Chapter & Level", 185);
        addHeaderCell(tableHeader, "Mini-Games", 105);
        addHeaderCell(tableHeader, "Daily Quests", 105);
        addHeaderCell(tableHeader, "Non-Daily", 95);

        this.add(tableHeader).fillX().padBottom(4).row();


        rowsTable = new Table();
        rowsTable.top().left();

        refreshRows();

        ScrollPane scrollPane = new ScrollPane(rowsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setOverscroll(false, false);

        this.add(scrollPane).size(720, 270).fill().expand().padBottom(15).row();


        TextButton closeBtn = new TextButton("Close", skin, "brown");
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClose != null) onClose.run();
            }
        });

        this.add(closeBtn).width(140).height(45).center();
    }

    private void addHeaderCell(Table table, String text, float width) {
        Label label = new Label(text, skin, "medium");
        label.setFontScale(0.75f);
        label.setColor(new Color(0.95f, 0.85f, 0.3f, 1f));
        label.setAlignment(Align.center);
        table.add(label).width(width).pad(6);
    }

    private void refreshRows() {
        rowsTable.clearChildren();

        List<User> users = LeaderBoardMenu.getSortedUsers(selectedColumn, isAscending);
        User loggedInUser = UsersManager.getInstance().getLoggedInUser();
        String currentUsername = loggedInUser != null ? loggedInUser.getUserName() : "";

        int rank = 1;
        for (User u : users) {
            Table row = new Table();
            boolean isSelf = u.getUserName().equals(currentUsername);

            if (isSelf) {
                row.setBackground(skin.getDrawable("image_ui_quests_panel_edge_to_edge_ten"));
                row.setColor(0.3f, 0.7f, 0.3f, 0.8f);
            }

            UserProgress progress = u.getUserProgress();
            int miniGames = progress != null ? progress.getMiniGamesCompleted() : 0;
            int dailyQuests = progress != null ? progress.getDailyQuestsCompleted() : 0;
            int nonDailyQuests = progress != null ? progress.getNonDailyQuestsCompleted() : 0;
            String lastLvl = LeaderBoardMenu.getLastChapterAndLevel(u);

            Color textColor = isSelf ? Color.YELLOW : Color.BLACK;

            addRowCell(row, String.valueOf(rank), 45, textColor);
            addRowCell(row, u.getUserName(), 155, textColor);
            addRowCell(row, lastLvl, 185, textColor);
            addRowCell(row, String.valueOf(miniGames), 105, textColor);
            addRowCell(row, String.valueOf(dailyQuests), 105, textColor);
            addRowCell(row, String.valueOf(nonDailyQuests), 95, textColor);

            rowsTable.add(row).fillX().padTop(2).padBottom(2).row();
            rank++;
        }

        if (users.isEmpty()) {
            Label emptyLbl = new Label("No users found.", skin);
            emptyLbl.setColor(Color.DARK_GRAY);
            rowsTable.add(emptyLbl).center().pad(20);
        }
    }

    private void addRowCell(Table table, String text, float width, Color color) {
        Label label = new Label(text, skin, "medium");
        label.setFontScale(0.75f);
        label.setColor(color);
        label.setAlignment(Align.center);
        table.add(label).width(width).pad(4);
    }
}
