package com.test1.PlantsVsZombies.src.View.LibGDXViews;

public class ActiveReaction {
    public enum Category { TEXT, EMOJI, STICKER }

    public final Category category;
    public final int index;
    public final String fromLabel;
    public final float spawnStateTime;

    public ActiveReaction(Category category, int index, String fromLabel, float spawnStateTime) {
        this.category = category;
        this.index = index;
        this.fromLabel = fromLabel;
        this.spawnStateTime = spawnStateTime;
    }
}
