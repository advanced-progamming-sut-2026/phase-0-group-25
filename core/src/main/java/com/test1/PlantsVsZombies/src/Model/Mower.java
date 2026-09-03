
package com.test1.PlantsVsZombies.src.Model;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.MowerTriggeredEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;

import java.util.HashSet;
import java.util.Set;

public class Mower {
    private int row;
    private float x;
    private float y;
    private boolean isActivated = false;
    private boolean isDone = false;
    private float speed = 750f;
    private String currentAnimState = "idle";
    private final String animationPath = "768/INITIAL/MOWERS/MOWER_TUTORIAL/MOWER_TUTORIAL.PAM";
    private final Set<Zombie> crushedZombies = new HashSet<>();

    public Mower(int row, float startX, float startY) {
        this.row = row;
        this.x = startX;
        this.y = startY;
    }

    public void trigger() {
        if (!isActivated && !isDone) {
            this.isActivated = true;
            this.currentAnimState = "transition";
        }
    }

    public void update(float delta, GamePlay thisGame) {
        if (!isActivated || isDone) return;

        x += speed * delta;

        int killedCount = 0;
        for (Zombie z : thisGame.getGameZombies()) {
            if (!crushedZombies.contains(z) && z.getRow() == this.row) {
                if (Math.abs(z.getPosition().getX() - this.x) <= 60 || z.getPosition().getX() < this.x) {
                    crushedZombies.add(z);
                    z.takeDamage(2000);
                    z.setCurrentHP(0);
                    z.setAlive(false);
                    killedCount++;
                }
            }
        }

        if (killedCount > 0) {
            QuestManager.getInstance().notifyEvent(new MowerTriggeredEvent(killedCount));
        }

        if (x > 1950) {
            isDone = true;
            isActivated = false;
            crushedZombies.clear();
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getRow() { return row; }
    public boolean isActivated() { return isActivated; }
    public boolean isDone() { return isDone; }
    public boolean isUsed() { return isActivated || isDone; }
    public String getCurrentAnimState() { return currentAnimState; }
    public String getAnimationPath() { return animationPath; }
}
