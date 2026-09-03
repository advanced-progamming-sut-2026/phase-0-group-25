package com.test1.PlantsVsZombies.src.Model.MiniGames.VasebreakerGame;

import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Entity;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;

public abstract class Jar {
    private final Position position;
    private final Entity content;
    private boolean isBroken;

    public Jar(Position position, Entity content) {
        this.position = position;
        this.content = content;
        this.isBroken = false;
    }

    public Position getPosition() {
        return position;
    }

    public Entity getContent() {
        return content;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setBroken(boolean broken) {
        isBroken = broken;
    }
}
