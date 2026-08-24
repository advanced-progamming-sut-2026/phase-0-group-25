package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.BattlePlant;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Position;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Zombie;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.ZombieFactory;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class NightOps extends GamePlay {

    public NightOps(ChapterType chapterType, int level, int difficulty, User thisUser,
                    ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("night ops");

    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        applyIcyWind();

        checkingSunMakers();

        // Updating Zombies, Plant and Projectile and Dynamite (Deleting them if they're dead) :
        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();

            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                // passing cooldown
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());

                if (currentTile != null) {
                    System.out.printf("Plant %s at (%d, %d) is destroyed.\n", plant.getName(), plant.getColumn(), plant.getRow());
                    currentTile.getPlants().removeIf(p -> p.getName().equals(plant.getName()));
                }
                bp.remove();
            }
        }
        Iterator<Zombie> z = gameZombies.iterator();
        while (z.hasNext()) {
            Zombie zombie = z.next();

            if (!zombie.isAlive()) {
                killAward(this.thisUser);
                if (zombie.isHalated()) {
                    glowingAward(zombie.getPosition());
                }
                Position zPos = Position.getRowAndColumn(zombie.getPosition());
                System.out.printf("Zombie of type %s is dead at (%d, %d)\n",
                    zombie.getName(), (int) zPos.getX(), (int) zPos.getY());

                addKilledZombieCost(zombie.getWaveNum(), zombie.getCost());
                z.remove();
            } else {
                if (zombie.getCurrentHP() > 0) {
                    zombie.update();
                }
            }
        }
        updateZombieTiles();
        Iterator<Projectile> pj = projectiles.iterator();
        while (pj.hasNext()) {
            Projectile thisProjectile = pj.next();

            if (thisProjectile.isActive()) {
                thisProjectile.update();
            } else {
                pj.remove();
            }
        }
        Iterator<Dynamite> dy = dynamites.iterator();
        while (dy.hasNext()) {
            Dynamite thisDynamite = dy.next();

            thisDynamite.update();
        }

        for (BattlePlant battlePlant : plants) {
            battlePlant.setCurrentCoolDown(Math.max(battlePlant.getCurrentCoolDown() - 1, 0));
        }

        // Spawning zombies :
        if (timeToSpawn == 0) {
            timeToSpawn = getRandomTime();
            for (Wave thisWave : allWaves) {
                if (thisWave.hasZombiesLeftToSpawn()) {
                    if (!thisWave.getStarted()) {
                        if (thisWave instanceof FinalWave) {
                            System.out.println("The final wave has come.");
                            UIManager.showToast("FINAL WAVE IS APPROACHING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        } else {
                            System.out.printf("Wave %d started.\n", thisWave.getWaveNum());
                            UIManager.showToast("Wave " + thisWave.getWaveNum() + " has started!", "IMAGE_UI_GENERIC_VTB");
                        }
                        thisWave.setStarted(true);
                    }
                    String nameOfZ = thisWave.spawnNextZombie().getName();
                    Position positionOfZ;
                    int spawnY = getNextRandomY();

                    if (chapterType == ChapterType.ANCIENT_EGYPT && Math.random() <= 0.12) {
                        int targetCol = random.nextInt(3) + 5;
                        positionOfZ = new Position(getRealX(targetCol), getRealY(spawnY));
                        addSandstormEffect((float) positionOfZ.getX(), (float) positionOfZ.getY());
                        UIManager.showToast("Sandstorm Inbound! (Lane " + spawnY + ")", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                    } else {
                        positionOfZ = new Position(spawnX, getRealY(spawnY));
                    }

                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, positionOfZ);
                    System.out.printf("Zombie %s spawned at wave %d in lane %d which costed %d.\n",
                        nameOfZ, thisWave.getWaveNum(), spawnY, newZombie.getCost());

                    if (Math.random() <= 0.05) {
                        newZombie.setHalated(true);
                    }

                    newZombie.setWaveNum(thisWave.getWaveNum());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) {
                    break;
                }
            }
        }

        // Checking if the end of the game (Losing) + Activate Mowers :
        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;

            int zRow = zombie.getRow();
            float zX = (float) zombie.getPosition().getX();

            Mower currentMower = mowers.stream()
                .filter(m -> m.getRow() == zRow)
                .findFirst()
                .orElse(null);

            if (currentMower != null) {
                if (!currentMower.isUsed()) {
                    if (zX <= currentMower.getX() + 40) {
                        System.out.println("Lawn mower triggered in row: " + zRow);
                        currentMower.trigger();
                    }
                } else if (currentMower.isDone() && zX <= 390) {
                    System.out.println("The zombie ate your brain; LOSER!!!");
                    UsersManager.getInstance().addGamesPlayed();
                    this.isPaused = true;
                }
            }
        }

        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            onWin();
            System.out.println("Dear humanz, zis is not done yet; we will come back to eat your brainz, humanz.");
            this.isPaused = true;
        }
    }
}
