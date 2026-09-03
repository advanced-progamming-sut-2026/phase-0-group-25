package com.test1.PlantsVsZombies.src.View.LibGDXViews;

public record ActiveReaction(Category category, int index, String fromLabel, float spawnStateTime) {

    public enum Category {TEXT, EMOJI, STICKER}
}
