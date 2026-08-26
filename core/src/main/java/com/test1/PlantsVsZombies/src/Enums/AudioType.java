package com.test1.PlantsVsZombies.src.Enums;

public enum AudioType {
    MENU_MUSIC("musicAndSFX/1-127. MUSIC STARTUP.mp3", true),
    GAME_MUSIC("musicAndSFX/1-008. ancient egypt final wave loop 480403.mp3", true),
    PROJECTILE_SHOOT("musicAndSFX/3-421. pea collision AUDIO ALWAYS LOADED 369.mp3", false),
    PROJECTILE_HIT("musicAndSFX/3-424. pea hit AUDIO ALWAYS LOADED 306.mp3", false);

    private final String path;
    private final boolean isMusic;

    AudioType(String path, boolean isMusic) {
        this.path = path;
        this.isMusic = isMusic;
    }

    public String getPath() {
        return path;
    }

    public boolean isMusic() {
        return isMusic;
    }
}
