package src.Model;

import src.Model.GamePlayType.GamePlay;
import src.Model.PlantsAndZombies.Zombie;

public class Mower {
    private int y;
    private int x = 20;
    private boolean isUsed = false;

    public Mower(int y) {
        this.y = y;
    }

    public void killZombies(GamePlay thisGame) {
        isUsed = true;
        for (Zombie z : thisGame.getGameZombies()) {
            if (z.getPosition().getY() == y) {
                System.out.println(z.getName());
                z.setAlive(false);
            }
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
