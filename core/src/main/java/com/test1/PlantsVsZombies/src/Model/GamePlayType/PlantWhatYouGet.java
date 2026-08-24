package com.test1.PlantsVsZombies.src.Model.GamePlayType;

import com.test1.PlantsVsZombies.src.Enums.ChapterType;
import com.test1.PlantsVsZombies.src.Model.Mower;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.*;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Dynamite;
import com.test1.PlantsVsZombies.src.Model.PlantsAndZombies.Projectiles.Projectile;
import com.test1.PlantsVsZombies.src.Model.Tile;
import com.test1.PlantsVsZombies.src.Model.User.User;
import com.test1.PlantsVsZombies.src.Model.User.UsersManager;
import com.test1.PlantsVsZombies.src.Model.Wave.FinalWave;
import com.test1.PlantsVsZombies.src.Model.Wave.Wave;
import com.test1.PlantsVsZombies.src.View.LibGDXViews.UIManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class PlantWhatYouGet extends GamePlay {
    private boolean waveStarted = false;
    private final int INITIAL_SUN_BUDGET = 800;

    public PlantWhatYouGet(ChapterType chapterType, int level, int difficulty, User thisUser,
                           ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("use the plants that appear for you and don't let the zombies reach the house");
        this.mySuns = INITIAL_SUN_BUDGET;
    }

    public boolean isWaveStarted() {
        return waveStarted;
    }

    public void startWave() {
        if (!this.waveStarted) {
            this.waveStarted = true;
            UIManager.showToast("ZOMBIES ARE COMING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
        }
    }

    @Override
    public void sunMaker() {

    }

    @Override
    public void planting(BattlePlant plant, Position position) {
        super.planting(plant, position);

        if (!waveStarted && plant != null) {
            plant.setCurrentCoolDown(0);
        }
    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;


        if (!waveStarted) {
            for (BattlePlant p : plants) {
                p.setCurrentCoolDown(0);
            }
            return;
        }


        timeToSpawn = Math.max(timeToSpawn - 1, 0);
        applyIcyWind();
        checkingSunMakers();


        Iterator<BattlePlant> bp = gamePlants.iterator();
        while (bp.hasNext()) {
            BattlePlant plant = bp.next();
            if (plant.isAlive() && plant.getCurrentHP() > 0) {
                plant.update();
                plant.setCooldown(Math.max(plant.getCooldown() - 1, 0));
            } else {
                Tile currentTile = getTileByPosition(plant.getColumn(), plant.getRow());
                if (currentTile != null) {
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
            Projectile p = pj.next();
            if (p.isActive()) p.update();
            else pj.remove();
        }

        for (Dynamite d : dynamites) {
            d.update();
        }

        for (BattlePlant p : plants) {
            p.setCurrentCoolDown(Math.max(p.getCurrentCoolDown() - 1, 0));
        }


        if (timeToSpawn == 0) {
            timeToSpawn = getRandomTime();
            for (Wave thisWave : allWaves) {
                if (thisWave.hasZombiesLeftToSpawn()) {
                    if (!thisWave.getStarted()) {
                        if (thisWave instanceof FinalWave) {
                            UIManager.showToast("FINAL WAVE IS APPROACHING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        } else {
                            UIManager.showToast("Wave " + thisWave.getWaveNum() + " has started!", "IMAGE_UI_GENERIC_VTB");
                        }
                        thisWave.setStarted(true);
                    }

                    String nameOfZ = thisWave.spawnNextZombie().getName();
                    int spawnY = getNextRandomY();
                    Position pos = new Position(spawnX, getRealY(spawnY));
                    Zombie newZombie = ZombieFactory.createZombie(nameOfZ, pos);

                    if (Math.random() <= 0.05) newZombie.setHalated(true);
                    newZombie.setWaveNum(thisWave.getWaveNum());
                    this.gameZombies.add(newZombie);
                    thisWave.addZombieToSpawned(newZombie);
                }
                if (!thisWave.isReadyForNextWave()) break;
            }
        }


        for (Zombie zombie : gameZombies) {
            if (!zombie.isAlive()) continue;
            int zRow = zombie.getRow();
            float zX = (float) zombie.getPosition().getX();

            Mower mower = mowers.stream().filter(m -> m.getRow() == zRow).findFirst().orElse(null);
            if (mower != null) {
                if (!mower.isUsed() && zX <= mower.getX() + 40) {
                    mower.trigger();
                } else if (mower.isDone() && zX <= 390) {
                    UsersManager.getInstance().addGamesPlayed();
                    endGame(false);
                }
            }
        }

        if (checkingTheEndOfTheGame()) {
            onWin();
            endGame(true);
        }
    }
}
