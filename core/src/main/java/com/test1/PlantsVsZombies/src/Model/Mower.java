package com.test1.PlantsVsZombies.src.Model;

import com.test1.PlantsVsZombies.src.Model.GamePlayType.GamePlay;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.Quests.Events.MowerTriggeredEvent;
import com.test1.PlantsVsZombies.src.Model.Quests.QuestManager;

public class Mower {
    private int y;
    private int x = 20;
    private boolean isUsed = false;

    public Mower(int y) {
        this.y = y;
    }

    public void killZombies(GamePlay thisGame) {
        isUsed = true;
        int countKilledZonbies = 0;
        for (Zombie z : thisGame.getGameZombies()) {
            if (z.getPosition().getY() == thisGame.getRealY(y)) {
                System.out.println(z.getName());
                countKilledZonbies++;
                z.setAlive(false);
            }
        }
        if (countKilledZonbies > 0) {
            QuestManager.getInstance().notifyEvent(new MowerTriggeredEvent(countKilledZonbies));
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isUsed() {
        return isUsed;
    }
}
