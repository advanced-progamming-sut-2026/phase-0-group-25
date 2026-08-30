package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.Faction;
import com.test1.PlantsVsZombies.src.Model.MiniGames.IZombieGame.IZombie;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Network.Client.ServerConnection;
import com.test1.PlantsVsZombies.src.Network.MessageType;
import com.test1.PlantsVsZombies.src.Network.NetworkMessage;
import pvz.skin.BorderedTable;

import java.util.ArrayList;

public class IZombieLobbyDialog extends BorderedTable {

    public IZombieLobbyDialog(Skin skin, Runnable onClose) {
        this.pad(30);

        Label title = new Label("I, ZOMBIE - SELECT MODE", skin, "big");
        title.setColor(Color.BLACK);
        title.setFontScale(0.85f);
        this.add(title).colspan(2).padBottom(20).row();


        TextButton randomMatchBtn = new TextButton("Random Online Match", skin, "green");
        randomMatchBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UIManager.showToast("Searching for opponent...", "IMAGE_UI_GENERIC_VTB");
                new Thread(() -> {
                    NetworkMessage msg = NetworkMessage.request(1, MessageType.JOIN_MATCHMAKING_QUEUE);
                    ServerConnection.getInstance().sendRequest(msg);
                }).start();
            }
        });
        this.add(randomMatchBtn).colspan(2).width(350).padBottom(15).row();


        TextField targetUserField = new TextField("", skin);
        targetUserField.setMessageText("Enter Username to Challenge");
        this.add(targetUserField).width(250).padRight(10);

        TextButton challengeBtn = new TextButton("Challenge", skin, "green_small");
        challengeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String target = targetUserField.getText().trim();
                if (target.isEmpty()) {
                    UIManager.showToast("Please enter a username!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                    return;
                }
                new Thread(() -> {
                    NetworkMessage msg = NetworkMessage.request(1, MessageType.CHALLENGE_USER).put("targetUsername", target);
                    ServerConnection.getInstance().sendRequest(msg);
                }).start();
                UIManager.showToast("Challenge sent to " + target, "IMAGE_UI_GENERIC_VTB");
            }
        });
        this.add(challengeBtn).width(120).padBottom(20).row();


        TextButton couchPlayBtn = new TextButton("Local Couch Play (2P Single PC)", skin, "purple");
        couchPlayBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ArrayList<String> pDeck = new ArrayList<>();
                pDeck.add("PEASHOOTER"); pDeck.add("SUNFLOWER"); pDeck.add("WALL_NUT");
                ArrayList<String> zDeck = new ArrayList<>();
                zDeck.add("DEFAULT"); zDeck.add("CONE_HEAD"); zDeck.add("BUCKET_HEAD");

                IZombie couchGame = new IZombie(UsersManager.getInstance().getLoggedInUser(), Faction.PLANT, false, pDeck, zDeck);
                UIManager.changeScreen(new IZombieScreen(couchGame, true));
            }
        });
        this.add(couchPlayBtn).colspan(2).width(350).padBottom(15).row();


        TextButton closeBtn = new TextButton("Back", skin, "brown");
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (onClose != null) onClose.run();
            }
        });
        this.add(closeBtn).colspan(2).width(140).center();
    }
}
