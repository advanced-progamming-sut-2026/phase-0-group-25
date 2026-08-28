package com.test1.PlantsVsZombies.src.Audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.test1.PlantsVsZombies.src.Enums.AudioType;

import java.util.HashMap;

public class SoundManager {
    private static SoundManager instance;
    private final HashMap<AudioType, Sound> sfxMap = new HashMap<>();

    private Music currentBackGroundMusic = null;
    private AudioType currentBackGroundMusicType = null;

    private float musicVolume = 0.5f;
    private float sfxVolume = 1.0f;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;

    // Caps the max music output to 20% so computer master volume can be turned up
    private static final float MUSIC_MAX_CEILING = 0.20f;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    public void initSound() {
        for (AudioType type : AudioType.values()) {
            if (!type.isMusic()) {
                sfxMap.put(type, Gdx.audio.newSound(Gdx.files.internal(type.getPath())));
            }
        }
    }

    public void playSound(AudioType audioType) {
        if (!sfxEnabled || audioType.isMusic()) return;
        Sound sound = sfxMap.get(audioType);
        if (sound != null) {
            sound.play(sfxVolume);
        }
    }

    public void playBackGroundMusic(AudioType audioType) {
        if (!audioType.isMusic()) return;

        if (currentBackGroundMusic != null && currentBackGroundMusicType == audioType) {
            updateBackGroundMusicState();
            return;
        }

        if (currentBackGroundMusic != null) {
            currentBackGroundMusic.stop();
            currentBackGroundMusic.dispose();
        }

        currentBackGroundMusicType = audioType;
        currentBackGroundMusic = Gdx.audio.newMusic(Gdx.files.internal(audioType.getPath()));
        currentBackGroundMusic.setLooping(true);
        updateBackGroundMusicState();
    }

    public void setMusicVolume(float volume) {
        musicVolume = Math.max(0f, Math.min(1f, volume));
        updateBackGroundMusicState();
    }

    public void setSfxVolume(float volume) {
        sfxVolume = Math.max(0f, Math.min(1f, volume));
    }

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        updateBackGroundMusicState();
    }

    public void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    private void updateBackGroundMusicState() {
        if (currentBackGroundMusic != null) {
            if (musicEnabled) {
                // Scales the 0.0-1.0 slider to output a maximum of 0.20 (20%)
                currentBackGroundMusic.setVolume(musicVolume * MUSIC_MAX_CEILING);
                if (!currentBackGroundMusic.isPlaying()) currentBackGroundMusic.play();
            } else {
                currentBackGroundMusic.setVolume(0f);
                currentBackGroundMusic.pause();
            }
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }

    public void dispose() {
        if (currentBackGroundMusic != null) {
            currentBackGroundMusic.dispose();
        }
        for (Sound sound : sfxMap.values()) {
            sound.dispose();
        }
        sfxMap.clear();
    }
}
