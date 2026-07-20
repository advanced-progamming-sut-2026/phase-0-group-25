package src.Model.GamePlayType;

import src.Enums.ChapterType;
import src.Model.Mower;
import src.Model.PlantsAndZombies.*;
import src.Model.PlantsAndZombies.Projectiles.Projectile;
import src.Model.User.User;
import src.Model.Wave.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Simple extends GamePlay {
    // plants that can appear in the game...
    private ArrayList<BattlePlant> myPlants;

    public Simple(ChapterType chapterType, int level, int difficulty, User thisUser, ArrayList<BattlePlant> myPlants) {
        super(chapterType, level, difficulty, thisUser);
        this.myPlants = myPlants;
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;

        if (this.level != 4) {
            sunMaker();
        }

        // Updating Zombies, Plant and Projectile (Deleting them if they're dead) :
        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if(plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                // passing cooldown
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                bp.remove();
            }
        }
        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive() || zombie.getCurrentHP() <= 0) {
                killAward(this.thisUser);
                glowingAward(this);
                z.remove();
            } else {
                zombie.update();
            }
        }
        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile thisProjectile = pj.next();

            if (thisProjectile.isActive()) {
                thisProjectile.update();
            } else {
                pj.remove();
            }
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
        for(Zombie zombie : gameZombies) {
            int yOfz = (int) zombie.getPosition().getY();
            int xOfz = (int) zombie.getPosition().getX();
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
