package Model;

import Model.GamePlayType.GamePlay;
import Model.PlantsAndZombies.Position;
import Model.PlantsAndZombies.Zombie;

public class Mower {
    private final int y;
    private boolean isUsed = false;

    public Mower(int y) {
        this.y = y;
    }

    public void killZombies(GamePlay thisGame){
        isUsed = true;
        for (Zombie z : thisGame.getGameZombies()) {
            if (z.getPosition().getY() == y) {
                z.setAlive(false);
            }
        }
    }

    public void update(GamePlay thisGame) {
        for (Zombie z : thisGame.getGameZombies()) {
            if (z.getPosition().getX() <= 0) {
                if (isUsed) {

                } else {
                    System.out.println("The lawn mower in the row <r> is triggered and killed these zombies:");
                    killZombies(thisGame);
                }
            }
        }
    }
}
