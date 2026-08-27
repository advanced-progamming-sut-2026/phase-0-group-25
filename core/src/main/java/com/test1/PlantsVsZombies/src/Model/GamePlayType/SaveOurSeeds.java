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

public class SaveOurSeeds extends GamePlay {
    private boolean isSetted = false;

    public SaveOurSeeds(ChapterType chapterType, int level, int difficulty, User thisUser,
                        ArrayList<String> plants, ArrayList<String> zombies, Set<String> boosted) {
        super(chapterType, level, difficulty, thisUser, plants, zombies, boosted);
        setLevelObjectives("save the seeds");

    }

    @Override
    public void update() {
        if (isPaused) return;
        totalTicksPassed++;
        timeToSpawn = Math.max(timeToSpawn - 1, 0);

        if (!isSetted) {
            String thisPName1 = "SUNFLOWER";
            String thisPName2 = "SUNFLOWER";
            Position position1 = new Position(5, 2);
            Position position2 = new Position(5, 4);
            BattlePlant thisP1 = PlantFactory.createBattlePlant(thisPName1, getLevelOfPlant(thisPName1), position1);
            BattlePlant thisP2 = PlantFactory.createBattlePlant(thisPName2, getLevelOfPlant(thisPName2), position2);
            this.planting(thisP1, position1);
            this.planting(thisP2, position2);

            this.isSetted = true;
            UIManager.showToast("Level Started! Protect your lawn!", "IMAGE_UI_GENERIC_VTB");
        }

        if (this.chapterType != ChapterType.DARK_AGE) {
            sunMaker();
        }
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
                    currentTile.getPlants().removeIf(p -> p.getName().equals(plant.getName()));
                }
                incrementLostPlants();
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
                            UIManager.showToast("FINAL WAVE IS APPROACHING!", "IMAGE_UI_GENERIC_TIMER_RIBBON_RED");
                        } else {
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
                        currentMower.trigger();
                    }
                } else if (currentMower.isDone() && zX <= 390) {
                    UsersManager.getInstance().addGamesPlayed();
                    endGame(false);
                }
            }
        }

        // Another condition for losing (in this game) :
        if (!canSaved()) {
            UsersManager.getInstance().addGamesPlayed();
            endGame(false);
        }

        // Checking if the end of the game (Winning) :
        if (checkingTheEndOfTheGame()) {
            onWin();
            endGame(true);
        }
    }

    private boolean canSaved() {
        Tile tile1 = getTileByPosition(5, 2);
        Tile tile2 = getTileByPosition(5, 4);

        return !tile1.getPlants().isEmpty() && !tile2.getPlants().isEmpty();
    }
}
