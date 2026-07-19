package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.Wave.*;

import java.util.ArrayList;

public class Simple extends GamePlay {
    // plants that can appear in the game...
    private ArrayList<BattlePlant> myPlants;

    public Simple(ChapterType chapterType, int level, ArrayList<BattlePlant> myPlants) {
        super(chapterType, level);
        this.myPlants = myPlants;
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        sunMaker();

        // Updating Zombies, Plant and Projectile :
        for (BattlePlant plant : gamePlants) {
            if(plant.isAlive()) plant.update();
            // passing cooldown
            plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
        }
        for (Zombie zombie : gameZombies) {
            if(zombie.isAlive()) zombie.update();
        }
        for(Projectile projectile : projectiles) {
            projectile.update();
        }

        // Spawning zombies :
        for (Wave thisWave : allWaves) {
            if (thisWave.hasZombiesLeftToSpawn()) {
                if (!thisWave.getStarted()) {
                    if (thisWave instanceof FinalWave) {
                        System.out.println("The final wave has come.");
                    } else {
                        System.out.printf("Wave %d started.\n", thisWave.getWaveNum());
                    }
                    thisWave.setStarted(true);
                }
                thisWave.spawnNextZombie(); // TODO : how to spaw...?
            }
            if (!thisWave.isReadyForNextWave()) {
                break;
            }
        }

        // Checking if the end of the game (Losing) + Activate Mowers :
        int x = mowers.get(0).getX();
        for(Zombie z : gameZombies) {
            int yOfz = (int) z.getPosition().getY();
            int xOfz = (int) z.getPosition().getX();
            Mower thisMower = mowers.stream().filter(p -> p.getY() == yOfz).findFirst().get();

            if (xOfz <= x) {
                if (!thisMower.isUsed()) {
                    System.out.println("The lawn mower in the row" + yOfz + "is triggered and killed these zombies:");
                    thisMower.killZombies(this);
                }
                else {
                    System.out.println("The zombie ate your brain; LOSER!!!");
                }
            }
        }

        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
        }
    }

}
