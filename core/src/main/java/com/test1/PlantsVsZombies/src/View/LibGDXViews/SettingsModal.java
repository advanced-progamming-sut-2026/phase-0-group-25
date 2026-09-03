package com.test1.PlantsVsZombies.src.View.LibGDXViews;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.test1.PlantsVsZombies.src.Audio.SoundManager;

public class SettingsModal extends Dialog {

    public SettingsModal(Skin skin) {
        super("Settings", skin);

        Table contentTable = getContentTable();
        contentTable.pad(20);


        Label volumeLabel = new Label("Music Volume:", skin);
        Slider volumeSlider = new Slider(0f, 1f, 0.05f, false, skin);
        volumeSlider.setValue(SoundManager.getInstance().getMusicVolume());
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setMusicVolume(volumeSlider.getValue());
            }
        });


        CheckBox musicCheckBox = new CheckBox(" Enable Music", skin);
        musicCheckBox.setChecked(SoundManager.getInstance().isMusicEnabled());
        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setMusicEnabled(musicCheckBox.isChecked());
            }
        });


        CheckBox sfxCheckBox = new CheckBox(" Enable SFX", skin);
        sfxCheckBox.setChecked(SoundManager.getInstance().isSfxEnabled());
        sfxCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().setSfxEnabled(sfxCheckBox.isChecked());
            }
        });

        contentTable.add(volumeLabel).left().pad(10);
        contentTable.add(volumeSlider).width(200).pad(10).row();
        contentTable.add(musicCheckBox).left().pad(10).row();
        contentTable.add(sfxCheckBox).left().pad(10).row();

        button("Close", true);
    }
}
